package com.openmind.order.application.commands.cancelorder;

import com.openmind.order.domain.repositories.OrderRepository;
import com.openmind.shared.domain.EntityNotFoundException;
import org.axonframework.commandhandling.CommandHandler;
import org.springframework.stereotype.Component;

@Component
public class CancelOrderCommandHandler {

    private final OrderRepository orderRepository;

    public CancelOrderCommandHandler(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @CommandHandler
    public void handle(CancelOrderCommand command) {
        var order = orderRepository.findById(command.orderId())
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));
        order.cancel(command.reason(), command.correlationId());
        orderRepository.update(order);
    }
}
