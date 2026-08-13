package com.openmind.order.domain.valueobjects;

import jakarta.persistence.Embeddable;

import java.util.UUID;

@Embeddable
public record CustomerId(UUID value) {

    public CustomerId {
        if (value == null || value.equals(new UUID(0L, 0L))) {
            throw new IllegalArgumentException("Id cannot be empty");
        }
    }

    public static CustomerId from(UUID value) {
        return new CustomerId(value);
    }

    public static CustomerId create() {
        return new CustomerId(UUID.randomUUID());
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
