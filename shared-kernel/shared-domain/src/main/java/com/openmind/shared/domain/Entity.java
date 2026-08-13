package com.openmind.shared.domain;

import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Transient;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Base class for all domain entities following DDD tactical patterns.
 */
@MappedSuperclass
public abstract class Entity {

    @Id
    private UUID id;

    private Instant createdAt = Instant.now();
    private Instant updatedAt;

    @Transient
    private final List<DomainEvent> domainEvents = new ArrayList<>();

    protected Entity() {
    }

    protected Entity(UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return id;
    }

    protected void setId(UUID id) {
        this.id = id;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    protected void setUpdatedAt() {
        this.updatedAt = Instant.now();
    }

    /**
     * Checks a business rule and throws if it's broken.
     */
    protected static void checkRule(BusinessRule rule) {
        if (rule.isBroken()) {
            throw new BusinessRuleValidationException(rule);
        }
    }

    protected void emit(DomainEvent domainEvent) {
        domainEvents.add(domainEvent);
    }

    protected List<DomainEvent> pendingDomainEvents() {
        return domainEvents;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Entity other) || getClass() != other.getClass()) {
            return false;
        }
        return id != null && Objects.equals(id, other.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
