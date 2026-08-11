package com.openmind.order.application.commands.markorderaspaymentcompleted;

import com.openmind.order.domain.aggregates.Order;
import com.openmind.order.domain.enums.OrderStatus;
import com.openmind.order.domain.repositories.OrderRepository;
import com.openmind.shared.application.commands.CommandHandler;
import com.openmind.shared.application.commands.CommandResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class MarkOrderAsPaymentCompletedCommandHandler implements CommandHandler<MarkOrderAsPaymentCompletedCommand, Void> {

    private static final Logger log = LoggerFactory.getLogger(MarkOrderAsPaymentCompletedCommandHandler.class);

    private final OrderRepository orderRepository;

    public MarkOrderAsPaymentCompletedCommandHandler(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public CommandResult<Void> handle(MarkOrderAsPaymentCompletedCommand command) {
        try {
            return orderRepository.findById(command.orderId())
                    .map(order -> {
                        ensurePaymentProcessing(order);
                        order.setPaymentCompleted(command.transactionId(), command.correlationId());
                        orderRepository.update(order);
                        return CommandResult.<Void>success(null);
                    })
                    .orElseGet(() -> CommandResult.failure("Order not found", "ORDER_NOT_FOUND"));
        } catch (Exception e) {
            log.error("[MarkOrderAsPaymentCompleted] ERROR: {}", e.getMessage(), e);
            return CommandResult.failure(e.getMessage(), "MARK_ORDER_PAYMENT_COMPLETED_FAILED");
        }
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
