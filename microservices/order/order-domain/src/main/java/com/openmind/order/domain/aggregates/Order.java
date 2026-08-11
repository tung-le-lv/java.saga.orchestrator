package com.openmind.order.domain.aggregates;

import com.openmind.order.domain.entities.OrderItem;
import com.openmind.order.domain.enums.OrderStatus;
import com.openmind.order.domain.events.OrderBackOrderedDomainEvent;
import com.openmind.order.domain.events.OrderCancelledDomainEvent;
import com.openmind.order.domain.events.OrderCreatedDomainEvent;
import com.openmind.order.domain.events.OrderItemAddedDomainEvent;
import com.openmind.order.domain.events.OrderPaymentCompletedDomainEvent;
import com.openmind.order.domain.events.OrderPaymentFailedDomainEvent;
import com.openmind.order.domain.events.OrderRefundedDomainEvent;
import com.openmind.order.domain.events.OrderShippedDomainEvent;
import com.openmind.order.domain.rules.OrderMustBeInOneOfStatusesRule;
import com.openmind.order.domain.rules.OrderMustBeInStatusRule;
import com.openmind.order.domain.valueobjects.Address;
import com.openmind.order.domain.valueobjects.CustomerId;
import com.openmind.order.domain.valueobjects.Money;
import com.openmind.shared.domain.AggregateRoot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Order aggregate root following DDD tactical patterns.
 */
public class Order extends AggregateRoot {

    private List<OrderItem> items = new ArrayList<>();

    private CustomerId customerId;
    private OrderStatus status;
    private Address shippingAddress;
    private Money totalAmount;
    private String cancellationReason;
    private String paymentTransactionId;
    private String trackingNumber;

    // Required for MongoDB deserialization
    protected Order() {
        super();
        this.customerId = CustomerId.create();
        this.status = OrderStatus.PENDING;
        this.shippingAddress = Address.create("Default", "Default", "Default", "00000", "Default");
        this.totalAmount = Money.zero();
    }

    private Order(UUID id, CustomerId customerId, Address shippingAddress) {
        super(id);
        this.customerId = customerId;
        this.shippingAddress = shippingAddress;
        this.status = OrderStatus.PENDING;
        this.totalAmount = Money.zero();
    }

    public static Order create(UUID orderId, CustomerId customerId, Address shippingAddress) {
        Order order = new Order(orderId, customerId, shippingAddress);
        order.emit(new OrderCreatedDomainEvent(orderId, customerId.getValue()));
        return order;
    }

    public void addItem(OrderItem item) {
        items.add(item);
        recalculateTotal();
        emit(new OrderItemAddedDomainEvent(getId(), item.getProductId(), item.getQuantity()));
    }

    public void setPaymentProcessing() {
        checkRule(new OrderMustBeInStatusRule(status, OrderStatus.PENDING, "transition to PaymentProcessing"));

        status = OrderStatus.PAYMENT_PROCESSING;
        setUpdatedAt();
    }

    public void setPaymentCompleted(String transactionId, UUID correlationId) {
        checkRule(new OrderMustBeInStatusRule(status, OrderStatus.PAYMENT_PROCESSING, "transition to PaymentCompleted"));

        status = OrderStatus.PAYMENT_COMPLETED;
        paymentTransactionId = transactionId;
        setUpdatedAt();

        emit(new OrderPaymentCompletedDomainEvent(getId(), transactionId, correlationId));
    }

    public void setPaymentFailed(String reason, UUID correlationId) {
        checkRule(new OrderMustBeInStatusRule(status, OrderStatus.PAYMENT_PROCESSING, "transition to PaymentFailed"));

        status = OrderStatus.PAYMENT_FAILED;
        cancellationReason = reason;
        setUpdatedAt();

        emit(new OrderPaymentFailedDomainEvent(getId(), reason, correlationId));
    }

    public void setFulfilling() {
        checkRule(new OrderMustBeInStatusRule(status, OrderStatus.PAYMENT_COMPLETED, "transition to Fulfilling"));

        status = OrderStatus.FULFILLING;
        setUpdatedAt();
    }

    public void setShipped(String trackingNumber, UUID correlationId) {
        checkRule(new OrderMustBeInStatusRule(status, OrderStatus.FULFILLING, "transition to Shipped"));

        status = OrderStatus.SHIPPED;
        this.trackingNumber = trackingNumber;
        setUpdatedAt();
        emit(new OrderShippedDomainEvent(getId(), trackingNumber, correlationId));
    }

    public void setBackOrdered(String reason, UUID correlationId) {
        checkRule(new OrderMustBeInStatusRule(status, OrderStatus.FULFILLING, "transition to BackOrdered"));

        status = OrderStatus.BACK_ORDERED;
        cancellationReason = reason;
        setUpdatedAt();
        emit(new OrderBackOrderedDomainEvent(getId(), reason, correlationId));
    }

    public void cancel(String reason, UUID correlationId) {
        List<OrderStatus> allowedStatuses = List.of(OrderStatus.PENDING, OrderStatus.PAYMENT_FAILED, OrderStatus.BACK_ORDERED);
        checkRule(new OrderMustBeInOneOfStatusesRule(status, allowedStatuses, "cancel"));

        status = OrderStatus.CANCELLED;
        cancellationReason = reason;
        setUpdatedAt();

        emit(new OrderCancelledDomainEvent(getId(), reason, correlationId));
    }

    public void setRefunded() {
        List<OrderStatus> allowedStatuses = List.of(OrderStatus.BACK_ORDERED, OrderStatus.CANCELLED);
        checkRule(new OrderMustBeInOneOfStatusesRule(status, allowedStatuses, "refund"));

        status = OrderStatus.REFUNDED;
        setUpdatedAt();
        emit(new OrderRefundedDomainEvent(getId()));
    }

    private void recalculateTotal() {
        Money total = Money.zero();
        for (OrderItem item : items) {
            total = total.add(item.getTotalPrice());
        }
        totalAmount = total;
    }

    public List<OrderItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    public CustomerId getCustomerId() {
        return customerId;
    }

    public void setCustomerId(CustomerId customerId) {
        this.customerId = customerId;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public Address getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(Address shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public Money getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Money totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getCancellationReason() {
        return cancellationReason;
    }

    public void setCancellationReason(String cancellationReason) {
        this.cancellationReason = cancellationReason;
    }

    public String getPaymentTransactionId() {
        return paymentTransactionId;
    }

    public void setPaymentTransactionId(String paymentTransactionId) {
        this.paymentTransactionId = paymentTransactionId;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }
}
