package com.openmind.payment.domain.events;

import com.openmind.shared.domain.DomainEvent;

import java.util.UUID;

public class PaymentCompletedDomainEvent extends DomainEvent {

    private final UUID paymentId;
    private final UUID orderId;
    private final String transactionId;
    private final UUID correlationId;

    public PaymentCompletedDomainEvent(UUID paymentId, UUID orderId, String transactionId, UUID correlationId) {
        this.paymentId = paymentId;
        this.orderId = orderId;
        this.transactionId = transactionId;
        this.correlationId = correlationId;
    }

    public UUID getPaymentId() {
        return paymentId;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public UUID getCorrelationId() {
        return correlationId;
    }
}
