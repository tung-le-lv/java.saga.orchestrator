package com.openmind.payment.domain.aggregates;

import com.openmind.payment.domain.enums.PaymentStatus;
import com.openmind.payment.domain.events.PaymentCompletedDomainEvent;
import com.openmind.payment.domain.events.PaymentFailedDomainEvent;
import com.openmind.payment.domain.events.PaymentRefundedDomainEvent;
import com.openmind.payment.domain.rules.PaymentMustBeInOneOfStatusesRule;
import com.openmind.payment.domain.rules.PaymentMustBeInStatusRule;
import com.openmind.payment.domain.valueobjects.Money;
import com.openmind.shared.domain.AggregateRoot;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Payment aggregate root following DDD tactical patterns.
 */
@jakarta.persistence.Entity
@Table(name = "payments")
public class Payment extends AggregateRoot {

    @Column(nullable = false)
    private UUID orderId;

    @Column(nullable = false)
    private UUID customerId;

    @Embedded
    @AttributeOverride(name = "amount", column = @Column(name = "amount"))
    private Money amount;

    private String paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    private String transactionId;
    private String failureReason;
    private String failureCode;

    // Required by JPA
    protected Payment() {
        super();
        this.amount = Money.create(BigDecimal.ZERO);
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
                status, List.of(PaymentStatus.PENDING, PaymentStatus.FAILED), "complete"));

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

    public UUID getCustomerId() {
        return customerId;
    }

    public Money getAmount() {
        return amount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public String getFailureCode() {
        return failureCode;
    }
}
