package com.openmind.fulfillment.application.domaineventhandlers;

import com.openmind.fulfillment.contract.events.OrderShippedEvent;
import com.openmind.fulfillment.domain.events.OrderShippedDomainEvent;
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
        messagePublisher.publish(Topics.FULFILLMENT_EVENTS, new OrderShippedEvent(
                event.getCorrelationId(), event.getOrderId(), event.getFulfillmentId(),
                event.getTrackingNumber(), event.getEstimatedDelivery()));
    }
}
