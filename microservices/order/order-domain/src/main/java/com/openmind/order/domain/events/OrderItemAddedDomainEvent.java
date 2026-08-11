package com.openmind.order.domain.events;

import com.openmind.shared.domain.DomainEvent;

import java.util.UUID;

public class OrderItemAddedDomainEvent extends DomainEvent {

    private final UUID orderId;
    private final UUID productId;
    private final int quantity;

    public OrderItemAddedDomainEvent(UUID orderId, UUID productId, int quantity) {
        this.orderId = orderId;
        this.productId = productId;
        this.quantity = quantity;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public UUID getProductId() {
        return productId;
    }

    public int getQuantity() {
        return quantity;
    }
}
