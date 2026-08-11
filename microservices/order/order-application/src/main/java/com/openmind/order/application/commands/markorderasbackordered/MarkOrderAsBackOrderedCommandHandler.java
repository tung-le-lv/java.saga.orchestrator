package com.openmind.order.application.commands.markorderasbackordered;

import com.openmind.order.domain.aggregates.Order;
import com.openmind.order.domain.enums.OrderStatus;
import com.openmind.order.domain.repositories.OrderRepository;
import com.openmind.shared.application.commands.CommandHandler;
import com.openmind.shared.application.commands.CommandResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class MarkOrderAsBackOrderedCommandHandler implements CommandHandler<MarkOrderAsBackOrderedCommand, Void> {

    private static final Logger log = LoggerFactory.getLogger(MarkOrderAsBackOrderedCommandHandler.class);

    private final OrderRepository orderRepository;

    public MarkOrderAsBackOrderedCommandHandler(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public CommandResult<Void> handle(MarkOrderAsBackOrderedCommand command) {
        try {
            return orderRepository.findById(command.orderId())
                    .map(order -> {
                        ensureFulfilling(order);
                        order.setBackOrdered(command.reason(), command.correlationId());
                        orderRepository.update(order);
                        return CommandResult.<Void>success(null);
                    })
                    .orElseGet(() -> CommandResult.failure("Order not found", "ORDER_NOT_FOUND"));
        } catch (Exception e) {
            log.error("[MarkOrderAsBackOrdered] ERROR: {}", e.getMessage(), e);
            return CommandResult.failure(e.getMessage(), "MARK_ORDER_BACK_ORDERED_FAILED");
        }
    }

    // Fulfillment only reports OrderShipped/FulfillmentFailed against an order that's already
    // PaymentCompleted; the intermediate Fulfilling state has no dedicated trigger of its own
    // in this flow, so we transition through it here.
    private void ensureFulfilling(Order order) {
        if (order.getStatus().equals(OrderStatus.PAYMENT_COMPLETED)) {
            order.setFulfilling();
        }
    }
}
