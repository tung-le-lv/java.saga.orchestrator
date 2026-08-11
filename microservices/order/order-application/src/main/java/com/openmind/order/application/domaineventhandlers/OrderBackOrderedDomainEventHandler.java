package com.openmind.order.application.domaineventhandlers;

import com.openmind.order.contract.events.OrderBackOrderedEvent;
import com.openmind.order.domain.events.OrderBackOrderedDomainEvent;
import com.openmind.shared.messaging.MessagePublisher;
import com.openmind.shared.messaging.Topics;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class OrderBackOrderedDomainEventHandler {

    private final MessagePublisher messagePublisher;

    public OrderBackOrderedDomainEventHandler(MessagePublisher messagePublisher) {
        this.messagePublisher = messagePublisher;
    }

    @EventListener
    public void on(OrderBackOrderedDomainEvent event) {
        messagePublisher.publish(Topics.ORDER_EVENTS, new OrderBackOrderedEvent(
                event.getCorrelationId(), event.getOrderId(), event.getReason()));
    }
}
