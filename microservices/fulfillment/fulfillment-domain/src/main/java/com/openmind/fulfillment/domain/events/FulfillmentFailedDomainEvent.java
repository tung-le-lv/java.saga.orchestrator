package com.openmind.fulfillment.domain.events;

import com.openmind.shared.domain.DomainEvent;

import java.util.UUID;

public class FulfillmentFailedDomainEvent extends DomainEvent {

    private final UUID fulfillmentId;
    private final UUID orderId;
    private final String reason;
    private final UUID correlationId;

    public FulfillmentFailedDomainEvent(UUID fulfillmentId, UUID orderId, String reason, UUID correlationId) {
        this.fulfillmentId = fulfillmentId;
        this.orderId = orderId;
        this.reason = reason;
        this.correlationId = correlationId;
    }

    public UUID getFulfillmentId() {
        return fulfillmentId;
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
