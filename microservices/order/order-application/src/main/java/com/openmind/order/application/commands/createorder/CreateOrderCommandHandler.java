package com.openmind.order.application.commands.createorder;

import com.openmind.order.domain.aggregates.Order;
import com.openmind.order.domain.entities.OrderItem;
import com.openmind.order.domain.repositories.OrderRepository;
import com.openmind.order.domain.valueobjects.Address;
import com.openmind.order.domain.valueobjects.CustomerId;
import com.openmind.order.domain.valueobjects.Money;
import com.openmind.shared.application.commands.CommandHandler;
import com.openmind.shared.application.commands.CommandResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CreateOrderCommandHandler implements CommandHandler<CreateOrderCommand, UUID> {

    private static final Logger log = LoggerFactory.getLogger(CreateOrderCommandHandler.class);

    private final OrderRepository orderRepository;

    public CreateOrderCommandHandler(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public CommandResult<UUID> handle(CreateOrderCommand command) {
        try {
            CustomerId customerId = CustomerId.from(command.customerId());
            Address shippingAddress = Address.create(
                    command.street(), command.city(), command.state(), command.zipCode(), command.country());

            Order order = Order.create(command.orderId(), customerId, shippingAddress);

            for (OrderItemCommand item : command.items()) {
                OrderItem orderItem = OrderItem.create(
                        item.productId(), item.productName(), item.quantity(), Money.create(item.unitPrice()));
                order.addItem(orderItem);
            }

            orderRepository.add(order);

            return CommandResult.success(order.getId());
        } catch (Exception e) {
            log.error("[CreateOrder] ERROR: {}", e.getMessage(), e);
            return CommandResult.failure(e.getMessage(), "CREATE_ORDER_FAILED");
        }
    }
}
