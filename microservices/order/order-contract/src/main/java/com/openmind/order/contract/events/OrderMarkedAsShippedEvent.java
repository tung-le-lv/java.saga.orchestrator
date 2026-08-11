package com.openmind.order.contract.events;

import com.openmind.order.contract.OrderEvent;

import java.util.UUID;

public record OrderMarkedAsShippedEvent(
        UUID correlationId,
        UUID orderId,
        String trackingNumber) implements OrderEvent {

    @Override
    public UUID getCorrelationId() {
        return correlationId;
    }
}
