package com.openmind.fulfillment.domain.events;

import com.openmind.shared.domain.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public class OrderShippedDomainEvent extends DomainEvent {

    private final UUID fulfillmentId;
    private final UUID orderId;
    private final String trackingNumber;
    private final Instant estimatedDelivery;
    private final UUID correlationId;

    public OrderShippedDomainEvent(UUID fulfillmentId, UUID orderId, String trackingNumber,
                                    Instant estimatedDelivery, UUID correlationId) {
        this.fulfillmentId = fulfillmentId;
        this.orderId = orderId;
        this.trackingNumber = trackingNumber;
        this.estimatedDelivery = estimatedDelivery;
        this.correlationId = correlationId;
    }

    public UUID getFulfillmentId() {
        return fulfillmentId;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public Instant getEstimatedDelivery() {
        return estimatedDelivery;
    }

    public UUID getCorrelationId() {
        return correlationId;
    }
}
