package com.openmind.payment.application.domaineventhandlers;

import com.openmind.payment.contract.events.PaymentCompletedEvent;
import com.openmind.payment.domain.events.PaymentCompletedDomainEvent;
import com.openmind.shared.messaging.MessagePublisher;
import com.openmind.shared.messaging.Topics;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class PaymentCompletedDomainEventHandler {

    private final MessagePublisher messagePublisher;

    public PaymentCompletedDomainEventHandler(MessagePublisher messagePublisher) {
        this.messagePublisher = messagePublisher;
    }

    @EventListener
    public void on(PaymentCompletedDomainEvent event) {
        messagePublisher.publish(Topics.PAYMENT_EVENTS, new PaymentCompletedEvent(
                event.getCorrelationId(), event.getOrderId(), event.getPaymentId(), event.getTransactionId()));
    }
}
