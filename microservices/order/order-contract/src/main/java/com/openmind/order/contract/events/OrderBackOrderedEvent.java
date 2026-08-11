package com.openmind.order.contract.events;

import com.openmind.order.contract.OrderEvent;

import java.util.UUID;

public record OrderBackOrderedEvent(
        UUID correlationId,
        UUID orderId,
        String reason) implements OrderEvent {

    @Override
    public UUID getCorrelationId() {
        return correlationId;
    }
}
