package com.openmind.shared.domain;

import java.time.Instant;
import java.util.UUID;

/**
 * Base class for domain events. A domain event represents something that happened in the domain.
 */
public abstract class DomainEvent {

    private final UUID eventId = UUID.randomUUID();
    private final Instant occurredOn = Instant.now();

    public UUID getEventId() {
        return eventId;
    }

    public Instant getOccurredOn() {
        return occurredOn;
    }
}
