package com.openmind.order.application.commands.markorderaspaymentcompleted;

import com.openmind.order.domain.aggregates.Order;
import com.openmind.order.domain.enums.OrderStatus;
import com.openmind.order.domain.repositories.OrderRepository;
import com.openmind.shared.domain.EntityNotFoundException;
import org.axonframework.commandhandling.CommandHandler;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class MarkOrderAsPaymentCompletedCommandHandler {

    private static final Set<OrderStatus> RESUMABLE_BEFORE_PAYMENT_PROCESSING =
            Set.of(OrderStatus.PENDING, OrderStatus.PAYMENT_FAILED);

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

    // The saga drives payment directly from Pending/PaymentFailed -> PaymentCompleted (the
    // latter on a payment retry after PaymentNotPaid); there's no separate trigger for the
    // intermediate PaymentProcessing state in either flow, so we transition through it here to
    // keep the aggregate's state machine intact.
    private void ensurePaymentProcessing(Order order) {
        if (RESUMABLE_BEFORE_PAYMENT_PROCESSING.contains(order.getStatus())) {
            order.setPaymentProcessing();
        }
    }
}
