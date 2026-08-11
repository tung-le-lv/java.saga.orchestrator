package com.openmind.email.contract.events;

import com.openmind.email.contract.EmailEvent;

import java.util.UUID;

public record EmailSentEvent(
        UUID correlationId,
        UUID orderId,
        String emailType) implements EmailEvent {

    @Override
    public UUID getCorrelationId() {
        return correlationId;
    }
}
