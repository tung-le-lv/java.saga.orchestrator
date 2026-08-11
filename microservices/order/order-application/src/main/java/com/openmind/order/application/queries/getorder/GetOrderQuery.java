package com.openmind.order.application.queries.getorder;

import com.openmind.shared.application.queries.Query;

import java.util.UUID;

public record GetOrderQuery(UUID orderId) implements Query<OrderDto> {
}
