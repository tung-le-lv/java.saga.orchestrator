package com.openmind.order.application.commands.markorderaspaymentfailed;

import com.openmind.order.domain.aggregates.Order;
import com.openmind.order.domain.enums.OrderStatus;
import com.openmind.order.domain.repositories.OrderRepository;
import com.openmind.shared.application.commands.CommandHandler;
import com.openmind.shared.application.commands.CommandResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class MarkOrderAsPaymentFailedCommandHandler implements CommandHandler<MarkOrderAsPaymentFailedCommand, Void> {

    private static final Logger log = LoggerFactory.getLogger(MarkOrderAsPaymentFailedCommandHandler.class);

    private final OrderRepository orderRepository;

    public MarkOrderAsPaymentFailedCommandHandler(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public CommandResult<Void> handle(MarkOrderAsPaymentFailedCommand command) {
        try {
            return orderRepository.findById(command.orderId())
                    .map(order -> {
                        ensurePaymentProcessing(order);
                        order.setPaymentFailed(command.reason(), command.correlationId());
                        orderRepository.update(order);
                        return CommandResult.<Void>success(null);
                    })
                    .orElseGet(() -> CommandResult.failure("Order not found", "ORDER_NOT_FOUND"));
        } catch (Exception e) {
            log.error("[MarkOrderAsPaymentFailed] ERROR: {}", e.getMessage(), e);
            return CommandResult.failure(e.getMessage(), "MARK_ORDER_PAYMENT_FAILED_FAILED");
        }
    }

    private void ensurePaymentProcessing(Order order) {
        if (order.getStatus().equals(OrderStatus.PENDING)) {
            order.setPaymentProcessing();
        }
    }
}
