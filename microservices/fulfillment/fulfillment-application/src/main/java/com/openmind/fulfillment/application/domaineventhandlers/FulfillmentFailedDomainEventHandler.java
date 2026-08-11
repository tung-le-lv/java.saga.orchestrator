package com.openmind.fulfillment.application.domaineventhandlers;

import com.openmind.fulfillment.contract.events.FulfillmentFailedEvent;
import com.openmind.fulfillment.domain.events.FulfillmentFailedDomainEvent;
import com.openmind.shared.messaging.MessagePublisher;
import com.openmind.shared.messaging.Topics;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class FulfillmentFailedDomainEventHandler {

    private final MessagePublisher messagePublisher;

    public FulfillmentFailedDomainEventHandler(MessagePublisher messagePublisher) {
        this.messagePublisher = messagePublisher;
    }

    @EventListener
    public void on(FulfillmentFailedDomainEvent event) {
        messagePublisher.publish(Topics.FULFILLMENT_EVENTS, new FulfillmentFailedEvent(
                event.getCorrelationId(), event.getOrderId(), event.getReason()));
    }
}
