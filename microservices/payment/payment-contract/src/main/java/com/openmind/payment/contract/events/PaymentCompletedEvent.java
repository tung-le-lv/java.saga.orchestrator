package com.openmind.payment.contract.events;

import com.openmind.payment.contract.PaymentEvent;

import java.util.UUID;

public record PaymentCompletedEvent(
        UUID correlationId,
        UUID orderId,
        UUID paymentId,
        String transactionId) implements PaymentEvent {

    @Override
    public UUID getCorrelationId() {
        return correlationId;
    }
}
