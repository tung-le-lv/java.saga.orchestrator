package com.openmind.payment.application.domaineventhandlers;

import com.openmind.payment.contract.events.PaymentFailedEvent;
import com.openmind.payment.domain.events.PaymentFailedDomainEvent;
import com.openmind.shared.messaging.MessagePublisher;
import com.openmind.shared.messaging.Topics;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class PaymentFailedDomainEventHandler {

    private final MessagePublisher messagePublisher;

    public PaymentFailedDomainEventHandler(MessagePublisher messagePublisher) {
        this.messagePublisher = messagePublisher;
    }

    @EventListener
    public void on(PaymentFailedDomainEvent event) {
        messagePublisher.publish(Topics.PAYMENT_EVENTS, new PaymentFailedEvent(
                event.getCorrelationId(), event.getOrderId(), event.getReason(), event.getErrorCode()));
    }
}
