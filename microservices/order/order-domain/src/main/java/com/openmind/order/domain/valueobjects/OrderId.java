package com.openmind.order.domain.valueobjects;

import com.openmind.shared.domain.StronglyTypedId;

import java.util.UUID;

public final class OrderId extends StronglyTypedId<OrderId> {

    // Required for MongoDB deserialization
    protected OrderId() {
        super();
    }

    private OrderId(UUID value) {
        super(value);
    }

    public static OrderId from(UUID value) {
        return new OrderId(value);
    }

    public static OrderId create() {
        return new OrderId(UUID.randomUUID());
    }
}
