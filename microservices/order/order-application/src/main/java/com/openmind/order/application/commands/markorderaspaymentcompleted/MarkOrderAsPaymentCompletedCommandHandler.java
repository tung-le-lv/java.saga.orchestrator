package com.openmind.order.application.commands.markorderaspaymentcompleted;

import com.openmind.order.domain.aggregates.Order;
import com.openmind.order.domain.enums.OrderStatus;
import com.openmind.order.domain.repositories.OrderRepository;
import com.openmind.shared.domain.EntityNotFoundException;
import org.axonframework.commandhandling.CommandHandler;
import org.springframework.stereotype.Component;

@Component
public class MarkOrderAsPaymentCompletedCommandHandler {

    private final OrderRepository orderRepository;

    public MarkOrderAsPaymentCompletedCommandHandler(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @CommandHandler
    public void handle(MarkOrderAsPaymentCompletedCommand command) {
        var order = orderRepository.findById(command.orderId())
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));
        ensurePaymentProcessing(order);
        order.setPaymentCompleted(command.transactionId(), command.correlationId());
        orderRepository.update(order);
    }

    // The saga drives payment directly from Pending -> PaymentCompleted/Failed; there's no
    // separate trigger for the intermediate PaymentProcessing state in this flow, so we
    // transition through it here to keep the aggregate's state machine intact.
    private void ensurePaymentProcessing(Order order) {
        if (order.getStatus().equals(OrderStatus.PENDING)) {
            order.setPaymentProcessing();
        }
    }
}
