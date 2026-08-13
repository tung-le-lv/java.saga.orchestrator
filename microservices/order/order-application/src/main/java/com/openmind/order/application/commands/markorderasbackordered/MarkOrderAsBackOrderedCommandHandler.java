package com.openmind.order.application.commands.markorderasbackordered;

import com.openmind.order.domain.aggregates.Order;
import com.openmind.order.domain.enums.OrderStatus;
import com.openmind.order.domain.repositories.OrderRepository;
import com.openmind.shared.domain.EntityNotFoundException;
import org.axonframework.commandhandling.CommandHandler;
import org.springframework.stereotype.Component;

@Component
public class MarkOrderAsBackOrderedCommandHandler {

    private final OrderRepository orderRepository;

    public MarkOrderAsBackOrderedCommandHandler(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @CommandHandler
    public void handle(MarkOrderAsBackOrderedCommand command) {
        var order = orderRepository.findById(command.orderId())
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));
        ensureFulfilling(order);
        order.setBackOrdered(command.reason(), command.correlationId());
        orderRepository.update(order);
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
