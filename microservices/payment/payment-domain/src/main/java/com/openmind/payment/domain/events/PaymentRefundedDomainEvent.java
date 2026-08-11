package com.openmind.payment.domain.events;

import com.openmind.shared.domain.DomainEvent;

import java.util.UUID;

public class PaymentRefundedDomainEvent extends DomainEvent {

    private final UUID paymentId;
    private final UUID orderId;
    private final UUID correlationId;

    public PaymentRefundedDomainEvent(UUID paymentId, UUID orderId, UUID correlationId) {
        this.paymentId = paymentId;
        this.orderId = orderId;
        this.correlationId = correlationId;
    }

    public UUID getPaymentId() {
        return paymentId;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public UUID getCorrelationId() {
        return correlationId;
    }
}
