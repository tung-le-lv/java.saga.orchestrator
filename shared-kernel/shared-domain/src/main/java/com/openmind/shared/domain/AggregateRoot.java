package com.openmind.shared.domain;

import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Version;
import org.springframework.data.domain.AfterDomainEventPublication;
import org.springframework.data.domain.DomainEvents;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

/**
 * Base class for aggregate roots following DDD tactical patterns.
 * An aggregate root is the entry point to an aggregate cluster of domain objects.
 * <p>
 * Exposes {@link DomainEvents}/{@link AfterDomainEventPublication} so that any Spring Data
 * repository's {@code save(...)} automatically publishes and clears pending domain events -
 * no custom unit-of-work or dispatch pipeline required.
 */
@MappedSuperclass
public abstract class AggregateRoot extends Entity implements IsAggregateRoot {

    @Version
    private int version;

    protected AggregateRoot() {
        super();
    }

    protected AggregateRoot(UUID id) {
        super(id);
    }

    @Override
    public int getVersion() {
        return version;
    }

    @DomainEvents
    Collection<DomainEvent> domainEvents() {
        return Collections.unmodifiableList(pendingDomainEvents());
    }

    @AfterDomainEventPublication
    void clearDomainEvents() {
        pendingDomainEvents().clear();
    }
}
