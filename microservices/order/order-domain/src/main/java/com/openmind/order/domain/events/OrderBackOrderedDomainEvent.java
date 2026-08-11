package com.openmind.order.domain.events;

import com.openmind.shared.domain.DomainEvent;

import java.util.UUID;

public class OrderBackOrderedDomainEvent extends DomainEvent {

    private final UUID orderId;
    private final String reason;
    private final UUID correlationId;

    public OrderBackOrderedDomainEvent(UUID orderId, String reason, UUID correlationId) {
        this.orderId = orderId;
        this.reason = reason;
        this.correlationId = correlationId;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public String getReason() {
        return reason;
    }

    public UUID getCorrelationId() {
        return correlationId;
    }
}
