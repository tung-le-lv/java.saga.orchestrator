package com.openmind.order.domain.events;

import com.openmind.shared.domain.DomainEvent;

import java.util.UUID;

public class OrderCreatedDomainEvent extends DomainEvent {

    private final UUID orderId;
    private final UUID customerId;

    public OrderCreatedDomainEvent(UUID orderId, UUID customerId) {
        this.orderId = orderId;
        this.customerId = customerId;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public UUID getCustomerId() {
        return customerId;
    }
}
