package com.openmind.fulfillment.domain.aggregates;

import com.openmind.fulfillment.domain.entities.FulfillmentItem;
import com.openmind.fulfillment.domain.enums.FulfillmentStatus;
import com.openmind.fulfillment.domain.events.FulfillmentFailedDomainEvent;
import com.openmind.fulfillment.domain.events.OrderShippedDomainEvent;
import com.openmind.fulfillment.domain.rules.FulfillmentMustBeInStatusRule;
import com.openmind.shared.domain.AggregateRoot;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Fulfillment aggregate root following DDD tactical patterns.
 */
public class Fulfillment extends AggregateRoot {

    private UUID orderId;
    private UUID customerId;
    private List<FulfillmentItem> items = new ArrayList<>();
    private String shippingAddress;
    private FulfillmentStatus status;
    private String trackingNumber;
    private Instant estimatedDelivery;
    private String failureReason;

    // Required for MongoDB deserialization
    protected Fulfillment() {
        super();
        this.status = FulfillmentStatus.PENDING;
    }

    private Fulfillment(UUID id, UUID orderId, UUID customerId, String shippingAddress) {
        super(id);
        this.orderId = orderId;
        this.customerId = customerId;
        this.shippingAddress = shippingAddress;
        this.status = FulfillmentStatus.PENDING;
    }

    public static Fulfillment create(UUID fulfillmentId, UUID orderId, UUID customerId, String shippingAddress) {
        return new Fulfillment(fulfillmentId, orderId, customerId, shippingAddress);
    }

    public void addItem(FulfillmentItem item) {
        items.add(item);
    }

    public void ship(String trackingNumber, Instant estimatedDelivery, UUID correlationId) {
        checkRule(new FulfillmentMustBeInStatusRule(status, FulfillmentStatus.PENDING, "ship"));

        status = FulfillmentStatus.SHIPPED;
        this.trackingNumber = trackingNumber;
        this.estimatedDelivery = estimatedDelivery;
        setUpdatedAt();

        emit(new OrderShippedDomainEvent(getId(), orderId, trackingNumber, estimatedDelivery, correlationId));
    }

    public void fail(String reason, UUID correlationId) {
        checkRule(new FulfillmentMustBeInStatusRule(status, FulfillmentStatus.PENDING, "fail"));

        status = FulfillmentStatus.FAILED;
        this.failureReason = reason;
        setUpdatedAt();

        emit(new FulfillmentFailedDomainEvent(getId(), orderId, reason, correlationId));
    }

    public void cancel(String reason) {
        checkRule(new FulfillmentMustBeInStatusRule(status, FulfillmentStatus.PENDING, "cancel"));

        status = FulfillmentStatus.CANCELLED;
        this.failureReason = reason;
        setUpdatedAt();
    }

    public UUID getOrderId() {
        return orderId;
    }

    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public void setCustomerId(UUID customerId) {
        this.customerId = customerId;
    }

    public List<FulfillmentItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public void setItems(List<FulfillmentItem> items) {
        this.items = items;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public FulfillmentStatus getStatus() {
        return status;
    }

    public void setStatus(FulfillmentStatus status) {
        this.status = status;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public Instant getEstimatedDelivery() {
        return estimatedDelivery;
    }

    public void setEstimatedDelivery(Instant estimatedDelivery) {
        this.estimatedDelivery = estimatedDelivery;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }
}
