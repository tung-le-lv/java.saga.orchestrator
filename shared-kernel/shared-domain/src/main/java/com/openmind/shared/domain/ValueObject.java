package com.openmind.shared.domain;

import java.util.List;
import java.util.Objects;

/**
 * Base class for value objects following DDD tactical patterns.
 * Value objects are immutable and compared by their components, not identity.
 */
public abstract class ValueObject {

    protected abstract List<Object> getEqualityComponents();

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        ValueObject other = (ValueObject) obj;
        return getEqualityComponents().equals(other.getEqualityComponents());
    }

    @Override
    public int hashCode() {
        return getEqualityComponents().stream()
                .map(Objects::hashCode)
                .reduce(0, (a, b) -> a ^ b);
    }
}
