package com.openmind.order.domain.valueobjects;

import jakarta.persistence.Embeddable;

import java.util.UUID;

@Embeddable
public record OrderId(UUID value) {

    public OrderId {
        if (value == null || value.equals(new UUID(0L, 0L))) {
            throw new IllegalArgumentException("Id cannot be empty");
        }
    }

    public static OrderId from(UUID value) {
        return new OrderId(value);
    }

    public static OrderId create() {
        return new OrderId(UUID.randomUUID());
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
