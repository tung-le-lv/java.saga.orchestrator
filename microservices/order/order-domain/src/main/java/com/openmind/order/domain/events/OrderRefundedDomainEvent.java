package com.openmind.order.domain.events;

import com.openmind.shared.domain.DomainEvent;

import java.util.UUID;

public class OrderRefundedDomainEvent extends DomainEvent {

    private final UUID orderId;

    public OrderRefundedDomainEvent(UUID orderId) {
        this.orderId = orderId;
    }

    public UUID getOrderId() {
        return orderId;
    }
}
