package com.openmind.order.contract.events;

import com.openmind.order.contract.OrderEvent;
import com.openmind.order.contract.OrderItemDto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record OrderValidatedEvent(
        UUID correlationId,
        UUID orderId,
        UUID customerId,
        BigDecimal totalAmount,
        String shippingAddress,
        String customerEmail,
        String customerName,
        List<OrderItemDto> items) implements OrderEvent {

    @Override
    public UUID getCorrelationId() {
        return correlationId;
    }
}
