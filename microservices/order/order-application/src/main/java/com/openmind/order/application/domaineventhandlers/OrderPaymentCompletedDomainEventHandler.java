package com.openmind.order.application.domaineventhandlers;

import com.openmind.order.contract.events.OrderPaymentCompletedEvent;
import com.openmind.order.domain.events.OrderPaymentCompletedDomainEvent;
import com.openmind.shared.messaging.MessagePublisher;
import com.openmind.shared.messaging.Topics;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Republishes {@link OrderPaymentCompletedDomainEvent} as the integration event
 * {@link OrderPaymentCompletedEvent} for any other interested consumer.
 */
@Component
public class OrderPaymentCompletedDomainEventHandler {

    private final MessagePublisher messagePublisher;

    public OrderPaymentCompletedDomainEventHandler(MessagePublisher messagePublisher) {
        this.messagePublisher = messagePublisher;
    }

    @EventListener
    public void on(OrderPaymentCompletedDomainEvent event) {
        messagePublisher.publish(Topics.ORDER_EVENTS, new OrderPaymentCompletedEvent(
                event.getCorrelationId(), event.getOrderId(), event.getTransactionId()));
    }
}
