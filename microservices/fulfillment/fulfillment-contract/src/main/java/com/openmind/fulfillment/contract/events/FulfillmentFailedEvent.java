package com.openmind.fulfillment.contract.events;

import com.openmind.fulfillment.contract.FulfillmentEvent;

import java.util.UUID;

public record FulfillmentFailedEvent(
        UUID correlationId,
        UUID orderId,
        String reason) implements FulfillmentEvent {

    @Override
    public UUID getCorrelationId() {
        return correlationId;
    }
}
