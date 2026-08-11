package com.openmind.shared.domain;

import java.util.UUID;

/**
 * Base class for aggregate roots following DDD tactical patterns.
 * An aggregate root is the entry point to an aggregate cluster of domain objects.
 */
public abstract class AggregateRoot extends Entity implements IsAggregateRoot {

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

    public void setVersion(int version) {
        this.version = version;
    }

    public void incrementVersion() {
        version++;
        setUpdatedAt();
    }
}
