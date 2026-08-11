package com.openmind.order.application.commands.cancelorder;

import com.openmind.order.domain.aggregates.Order;
import com.openmind.order.domain.repositories.OrderRepository;
import com.openmind.shared.application.commands.CommandHandler;
import com.openmind.shared.application.commands.CommandResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CancelOrderCommandHandler implements CommandHandler<CancelOrderCommand, Void> {

    private static final Logger log = LoggerFactory.getLogger(CancelOrderCommandHandler.class);

    private final OrderRepository orderRepository;

    public CancelOrderCommandHandler(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public CommandResult<Void> handle(CancelOrderCommand command) {
        try {
            return orderRepository.findById(command.orderId())
                    .map(order -> {
                        order.cancel(command.reason(), command.correlationId());
                        orderRepository.update(order);
                        return CommandResult.<Void>success(null);
                    })
                    .orElseGet(() -> CommandResult.failure("Order not found", "ORDER_NOT_FOUND"));
        } catch (Exception e) {
            log.error("[CancelOrder] ERROR: {}", e.getMessage(), e);
            return CommandResult.failure(e.getMessage(), "CANCEL_ORDER_FAILED");
        }
    }
}
