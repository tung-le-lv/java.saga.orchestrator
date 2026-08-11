package com.openmind.payment.domain.aggregates;

import com.openmind.payment.domain.enums.PaymentStatus;
import com.openmind.payment.domain.events.PaymentCompletedDomainEvent;
import com.openmind.payment.domain.events.PaymentFailedDomainEvent;
import com.openmind.payment.domain.events.PaymentRefundedDomainEvent;
import com.openmind.payment.domain.rules.PaymentMustBeInOneOfStatusesRule;
import com.openmind.payment.domain.rules.PaymentMustBeInStatusRule;
import com.openmind.payment.domain.valueobjects.Money;
import com.openmind.shared.domain.AggregateRoot;

import java.util.UUID;

/**
 * Payment aggregate root following DDD tactical patterns.
 */
public class Payment extends AggregateRoot {

    private UUID orderId;
    private UUID customerId;
    private Money amount;
    private String paymentMethod;
    private PaymentStatus status;
    private String transactionId;
    private String failureReason;
    private String failureCode;

    // Required for MongoDB deserialization
    protected Payment() {
        super();
        this.amount = Money.create(java.math.BigDecimal.ZERO);
        this.status = PaymentStatus.PENDING;
    }

    private Payment(UUID id, UUID orderId, UUID customerId, Money amount, String paymentMethod) {
        super(id);
        this.orderId = orderId;
        this.customerId = customerId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.status = PaymentStatus.PENDING;
    }

    public static Payment create(UUID paymentId, UUID orderId, UUID customerId, Money amount, String paymentMethod) {
        return new Payment(paymentId, orderId, customerId, amount, paymentMethod);
    }

    public void complete(String transactionId, UUID correlationId) {
        // Completing is valid both for a fresh payment (Pending) and for one being retried
        // after a prior decline (Failed) - see RetryPaymentCommandHandler.
        checkRule(new PaymentMustBeInOneOfStatusesRule(
                status, java.util.List.of(PaymentStatus.PENDING, PaymentStatus.FAILED), "complete"));

        status = PaymentStatus.COMPLETED;
        this.transactionId = transactionId;
        setUpdatedAt();

        emit(new PaymentCompletedDomainEvent(getId(), orderId, transactionId, correlationId));
    }

    public void fail(String reason, String errorCode, UUID correlationId) {
        checkRule(new PaymentMustBeInStatusRule(status, PaymentStatus.PENDING, "fail"));

        status = PaymentStatus.FAILED;
        this.failureReason = reason;
        this.failureCode = errorCode;
        setUpdatedAt();

        emit(new PaymentFailedDomainEvent(getId(), orderId, reason, errorCode, correlationId));
    }

    public void refund(UUID correlationId) {
        checkRule(new PaymentMustBeInStatusRule(status, PaymentStatus.COMPLETED, "refund"));

        status = PaymentStatus.REFUNDED;
        setUpdatedAt();

        emit(new PaymentRefundedDomainEvent(getId(), orderId, correlationId));
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

    public Money getAmount() {
        return amount;
    }

    public void setAmount(Money amount) {
        this.amount = amount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }

    public String getFailureCode() {
        return failureCode;
    }

    public void setFailureCode(String failureCode) {
        this.failureCode = failureCode;
    }
}
