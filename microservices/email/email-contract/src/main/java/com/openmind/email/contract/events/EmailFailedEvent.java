package com.openmind.email.contract.events;

import com.openmind.email.contract.EmailEvent;

import java.util.UUID;

public record EmailFailedEvent(
        UUID correlationId,
        UUID orderId,
        String reason) implements EmailEvent {

    @Override
    public UUID getCorrelationId() {
        return correlationId;
    }
}
