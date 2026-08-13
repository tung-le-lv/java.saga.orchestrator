package com.openmind.order.application.commands.markorderaspaymentfailed;

import com.openmind.order.domain.aggregates.Order;
import com.openmind.order.domain.enums.OrderStatus;
import com.openmind.order.domain.repositories.OrderRepository;
import com.openmind.shared.domain.EntityNotFoundException;
import org.axonframework.commandhandling.CommandHandler;
import org.springframework.stereotype.Component;

@Component
public class MarkOrderAsPaymentFailedCommandHandler {

    private final OrderRepository orderRepository;

    public MarkOrderAsPaymentFailedCommandHandler(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @CommandHandler
    public void handle(MarkOrderAsPaymentFailedCommand command) {
        var order = orderRepository.findById(command.orderId())
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));
        ensurePaymentProcessing(order);
        order.setPaymentFailed(command.reason(), command.correlationId());
        orderRepository.update(order);
    }

    private void ensurePaymentProcessing(Order order) {
        if (order.getStatus().equals(OrderStatus.PENDING)) {
            order.setPaymentProcessing();
        }
    }
}
