package com.openmind.shared.domain;

import java.util.List;
import java.util.UUID;

/**
 * Base class for strongly typed identifiers.
 * Provides type safety for entity identifiers and prevents mixing IDs of different entity types.
 */
public abstract class StronglyTypedId<T extends StronglyTypedId<T>> extends ValueObject {

    private static final UUID EMPTY = new UUID(0L, 0L);

    private UUID value;

    // Required for MongoDB deserialization
    protected StronglyTypedId() {
        this.value = EMPTY;
    }

    protected StronglyTypedId(UUID value) {
        if (value == null || EMPTY.equals(value)) {
            throw new IllegalArgumentException("Id cannot be empty");
        }
        this.value = value;
    }

    public UUID getValue() {
        return value;
    }

    public void setValue(UUID value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value.toString();
    }

    @Override
    protected List<Object> getEqualityComponents() {
        return List.of(value);
    }
}
