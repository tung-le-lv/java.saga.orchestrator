package com.openmind.order.application.domaineventhandlers;

import com.openmind.order.contract.events.OrderPaymentFailedEvent;
import com.openmind.order.domain.events.OrderPaymentFailedDomainEvent;
import com.openmind.shared.messaging.MessagePublisher;
import com.openmind.shared.messaging.Topics;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class OrderPaymentFailedDomainEventHandler {

    private final MessagePublisher messagePublisher;

    public OrderPaymentFailedDomainEventHandler(MessagePublisher messagePublisher) {
        this.messagePublisher = messagePublisher;
    }

    @EventListener
    public void on(OrderPaymentFailedDomainEvent event) {
        messagePublisher.publish(Topics.ORDER_EVENTS, new OrderPaymentFailedEvent(
                event.getCorrelationId(), event.getOrderId(), event.getReason()));
    }
}
