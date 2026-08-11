package com.openmind.order.contract;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Wire-level representation of an order line item, used inside integration
 * commands/events (as opposed to the {@code OrderItem} domain entity).
 */
public record OrderItemDto(
        UUID productId,
        String productName,
        int quantity,
        BigDecimal unitPrice) {
}
