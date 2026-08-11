package com.openmind.order.application.domaineventhandlers;

import com.openmind.order.contract.events.OrderMarkedAsShippedEvent;
import com.openmind.order.domain.events.OrderShippedDomainEvent;
import com.openmind.shared.messaging.MessagePublisher;
import com.openmind.shared.messaging.Topics;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class OrderShippedDomainEventHandler {

    private final MessagePublisher messagePublisher;

    public OrderShippedDomainEventHandler(MessagePublisher messagePublisher) {
        this.messagePublisher = messagePublisher;
    }

    @EventListener
    public void on(OrderShippedDomainEvent event) {
        messagePublisher.publish(Topics.ORDER_EVENTS, new OrderMarkedAsShippedEvent(
                event.getCorrelationId(), event.getOrderId(), event.getTrackingNumber()));
    }
}
