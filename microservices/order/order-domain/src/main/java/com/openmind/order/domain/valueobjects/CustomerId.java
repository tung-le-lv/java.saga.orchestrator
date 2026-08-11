package com.openmind.order.domain.valueobjects;

import com.openmind.shared.domain.StronglyTypedId;

import java.util.UUID;

public final class CustomerId extends StronglyTypedId<CustomerId> {

    // Required for MongoDB deserialization
    protected CustomerId() {
        super();
    }

    private CustomerId(UUID value) {
        super(value);
    }

    public static CustomerId from(UUID value) {
        return new CustomerId(value);
    }

    public static CustomerId create() {
        return new CustomerId(UUID.randomUUID());
    }
}
