package com.openmind.payment.contract.events;

import com.openmind.payment.contract.PaymentEvent;

import java.util.UUID;

public record PaymentRefundedEvent(
        UUID correlationId,
        UUID orderId,
        UUID paymentId) implements PaymentEvent {

    @Override
    public UUID getCorrelationId() {
        return correlationId;
    }
}
