package com.openmind.payment.contract.events;

import com.openmind.payment.contract.PaymentEvent;

import java.util.UUID;

public record PaymentFailedEvent(
        UUID correlationId,
        UUID orderId,
        String reason,
        String errorCode) implements PaymentEvent {

    @Override
    public UUID getCorrelationId() {
        return correlationId;
    }
}
