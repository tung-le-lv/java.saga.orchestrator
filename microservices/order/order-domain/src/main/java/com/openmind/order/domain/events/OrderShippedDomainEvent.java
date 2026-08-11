package com.openmind.order.domain.events;

import com.openmind.shared.domain.DomainEvent;

import java.util.UUID;

public class OrderShippedDomainEvent extends DomainEvent {

    private final UUID orderId;
    private final String trackingNumber;
    private final UUID correlationId;

    public OrderShippedDomainEvent(UUID orderId, String trackingNumber, UUID correlationId) {
        this.orderId = orderId;
        this.trackingNumber = trackingNumber;
        this.correlationId = correlationId;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public UUID getCorrelationId() {
        return correlationId;
    }
}
