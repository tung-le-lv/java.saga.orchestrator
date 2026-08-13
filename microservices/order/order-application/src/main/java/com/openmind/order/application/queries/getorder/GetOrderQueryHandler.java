package com.openmind.order.application.queries.getorder;

import com.openmind.order.contract.OrderItemDto;
import com.openmind.order.domain.aggregates.Order;
import com.openmind.order.domain.entities.OrderItem;
import com.openmind.order.domain.repositories.OrderRepository;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;

@Component
public class GetOrderQueryHandler {

    private final OrderRepository orderRepository;

    public GetOrderQueryHandler(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @QueryHandler
    public OrderDto handle(GetOrderQuery query) {
        return orderRepository.findById(query.orderId())
                .map(this::toDto)
                .orElse(null);
    }

    private OrderDto toDto(Order order) {
        var items = order.getItems().stream()
                .map(this::toItemDto)
                .toList();

        return new OrderDto(
                order.getId(),
                order.getCustomerId().value(),
                order.getStatus().getDisplayName(),
                order.getTotalAmount().amount(),
                order.getShippingAddress().toString(),
                items);
    }

    private OrderItemDto toItemDto(OrderItem item) {
        return new OrderItemDto(item.getProductId(), item.getProductName(), item.getQuantity(), item.getUnitPrice().amount());
    }
}
