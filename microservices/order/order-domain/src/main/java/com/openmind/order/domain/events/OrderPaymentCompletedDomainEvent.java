package com.openmind.order.domain.events;

import com.openmind.shared.domain.DomainEvent;

import java.util.UUID;

public class OrderPaymentCompletedDomainEvent extends DomainEvent {

    private final UUID orderId;
    private final String transactionId;
    private final UUID correlationId;

    public OrderPaymentCompletedDomainEvent(UUID orderId, String transactionId, UUID correlationId) {
        this.orderId = orderId;
        this.transactionId = transactionId;
        this.correlationId = correlationId;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public UUID getCorrelationId() {
        return correlationId;
    }
}
