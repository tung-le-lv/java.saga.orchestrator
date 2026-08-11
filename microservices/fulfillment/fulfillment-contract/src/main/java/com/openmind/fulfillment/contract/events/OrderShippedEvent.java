package com.openmind.fulfillment.contract.events;

import com.openmind.fulfillment.contract.FulfillmentEvent;

import java.time.Instant;
import java.util.UUID;

public record OrderShippedEvent(
        UUID correlationId,
        UUID orderId,
        UUID fulfillmentId,
        String trackingNumber,
        Instant estimatedDelivery) implements FulfillmentEvent {

    @Override
    public UUID getCorrelationId() {
        return correlationId;
    }
}
