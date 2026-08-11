package com.openmind.order.contract.events;

import com.openmind.order.contract.OrderEvent;

import java.util.UUID;

/**
 * Published by the Order service after it records a payment as completed
 * (fan-out notification; the Orchestrator's saga instead reacts to the Payment
 * service's own {@code PaymentCompletedEvent}).
 */
public record OrderPaymentCompletedEvent(
        UUID correlationId,
        UUID orderId,
        String transactionId) implements OrderEvent {

    @Override
    public UUID getCorrelationId() {
        return correlationId;
    }
}
