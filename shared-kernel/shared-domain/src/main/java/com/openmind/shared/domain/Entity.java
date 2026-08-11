package com.openmind.shared.domain;

import org.bson.codecs.pojo.annotations.BsonIgnore;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Base class for all domain entities following DDD tactical patterns.
 * <p>
 * Ids are fixed to {@link UUID} (rather than kept generic like the original .NET
 * {@code Entity<TId>}) because every entity in this system uses one, and a generic id type
 * parameter erases at runtime in a way that defeats the MongoDB POJO codec's automatic
 * property-type resolution.
 */
public abstract class Entity {

    private UUID id;
    private Instant createdAt = Instant.now();
    private Instant updatedAt;

    private final List<DomainEvent> domainEvents = new ArrayList<>();

    protected Entity() {
    }

    protected Entity(UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return id;
    }

    // Public rather than protected: the MongoDB POJO codec's automatic property discovery
    // only sees public getter/setter pairs (java.lang.Class#getMethods()), so a non-public
    // setter is silently treated as encode-only and never called on decode.
    public void setId(UUID id) {
        this.id = id;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    protected void setUpdatedAt() {
        this.updatedAt = Instant.now();
    }

    @BsonIgnore
    public List<DomainEvent> getDomainEvents() {
        return Collections.unmodifiableList(domainEvents);
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

    public void removeDomainEvent(DomainEvent domainEvent) {
        domainEvents.remove(domainEvent);
    }

    public void clearDomainEvents() {
        domainEvents.clear();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Entity other)) {
            return false;
        }
        if (getClass() != other.getClass()) {
            return false;
        }
        return Objects.equals(id, other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
