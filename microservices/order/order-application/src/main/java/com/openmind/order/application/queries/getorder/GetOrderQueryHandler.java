package com.openmind.order.application.queries.getorder;

import com.openmind.order.contract.OrderItemDto;
import com.openmind.order.domain.aggregates.Order;
import com.openmind.order.domain.entities.OrderItem;
import com.openmind.order.domain.repositories.OrderRepository;
import com.openmind.shared.application.queries.QueryHandler;
import com.openmind.shared.application.queries.QueryResult;
import org.springframework.stereotype.Component;

@Component
public class GetOrderQueryHandler implements QueryHandler<GetOrderQuery, OrderDto> {

    private final OrderRepository orderRepository;

    public GetOrderQueryHandler(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public QueryResult<OrderDto> handle(GetOrderQuery query) {
        return orderRepository.findById(query.orderId())
                .map(this::toDto)
                .map(QueryResult::success)
                .orElseGet(() -> QueryResult.failure("Order not found"));
    }

    private OrderDto toDto(Order order) {
        var items = order.getItems().stream()
                .map(this::toItemDto)
                .toList();

        return new OrderDto(
                order.getId(),
                order.getCustomerId().getValue(),
                order.getStatus().getName(),
                order.getTotalAmount().getAmount(),
                order.getShippingAddress().toString(),
                items);
    }

    private OrderItemDto toItemDto(OrderItem item) {
        return new OrderItemDto(item.getProductId(), item.getProductName(), item.getQuantity(), item.getUnitPrice().getAmount());
    }
}
