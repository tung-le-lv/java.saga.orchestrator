package com.openmind.order.application.domaineventhandlers;

import com.openmind.order.contract.events.OrderCancelledEvent;
import com.openmind.order.domain.events.OrderCancelledDomainEvent;
import com.openmind.shared.messaging.MessagePublisher;
import com.openmind.shared.messaging.Topics;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class OrderCancelledDomainEventHandler {

    private final MessagePublisher messagePublisher;

    public OrderCancelledDomainEventHandler(MessagePublisher messagePublisher) {
        this.messagePublisher = messagePublisher;
    }

    @EventListener
    public void on(OrderCancelledDomainEvent event) {
        messagePublisher.publish(Topics.ORDER_EVENTS, new OrderCancelledEvent(
                event.getCorrelationId(), event.getOrderId(), event.getReason()));
    }
}
