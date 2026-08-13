package com.openmind.order.application.commands.markorderasshipped;

import com.openmind.order.domain.aggregates.Order;
import com.openmind.order.domain.enums.OrderStatus;
import com.openmind.order.domain.repositories.OrderRepository;
import com.openmind.shared.domain.EntityNotFoundException;
import org.axonframework.commandhandling.CommandHandler;
import org.springframework.stereotype.Component;

@Component
public class MarkOrderAsShippedCommandHandler {

    private final OrderRepository orderRepository;

    public MarkOrderAsShippedCommandHandler(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @CommandHandler
    public void handle(MarkOrderAsShippedCommand command) {
        var order = orderRepository.findById(command.orderId())
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));
        ensureFulfilling(order);
        order.setShipped(command.trackingNumber(), command.correlationId());
        orderRepository.update(order);
    }

    private void ensureFulfilling(Order order) {
        if (order.getStatus().equals(OrderStatus.PAYMENT_COMPLETED)) {
            order.setFulfilling();
        }
    }
}
