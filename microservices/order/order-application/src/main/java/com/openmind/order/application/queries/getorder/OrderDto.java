package com.openmind.order.application.queries.getorder;

import com.openmind.order.contract.OrderItemDto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record OrderDto(
        UUID id,
        UUID customerId,
        String status,
        BigDecimal totalAmount,
        String shippingAddress,
        List<OrderItemDto> items) {
}
