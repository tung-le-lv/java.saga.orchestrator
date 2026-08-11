package com.openmind.order.application.commands.markorderasshipped;

import com.openmind.order.domain.aggregates.Order;
import com.openmind.order.domain.enums.OrderStatus;
import com.openmind.order.domain.repositories.OrderRepository;
import com.openmind.shared.application.commands.CommandHandler;
import com.openmind.shared.application.commands.CommandResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class MarkOrderAsShippedCommandHandler implements CommandHandler<MarkOrderAsShippedCommand, Void> {

    private static final Logger log = LoggerFactory.getLogger(MarkOrderAsShippedCommandHandler.class);

    private final OrderRepository orderRepository;

    public MarkOrderAsShippedCommandHandler(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public CommandResult<Void> handle(MarkOrderAsShippedCommand command) {
        try {
            return orderRepository.findById(command.orderId())
                    .map(order -> {
                        ensureFulfilling(order);
                        order.setShipped(command.trackingNumber(), command.correlationId());
                        orderRepository.update(order);
                        return CommandResult.<Void>success(null);
                    })
                    .orElseGet(() -> CommandResult.failure("Order not found", "ORDER_NOT_FOUND"));
        } catch (Exception e) {
            log.error("[MarkOrderAsShipped] ERROR: {}", e.getMessage(), e);
            return CommandResult.failure(e.getMessage(), "MARK_ORDER_SHIPPED_FAILED");
        }
    }

    private void ensureFulfilling(Order order) {
        if (order.getStatus().equals(OrderStatus.PAYMENT_COMPLETED)) {
            order.setFulfilling();
        }
    }
}
