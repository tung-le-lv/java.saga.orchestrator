package com.openmind.payment.application.domaineventhandlers;

import com.openmind.payment.contract.events.PaymentRefundedEvent;
import com.openmind.payment.domain.events.PaymentRefundedDomainEvent;
import com.openmind.shared.messaging.MessagePublisher;
import com.openmind.shared.messaging.Topics;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class PaymentRefundedDomainEventHandler {

    private final MessagePublisher messagePublisher;

    public PaymentRefundedDomainEventHandler(MessagePublisher messagePublisher) {
        this.messagePublisher = messagePublisher;
    }

    @EventListener
    public void on(PaymentRefundedDomainEvent event) {
        messagePublisher.publish(Topics.PAYMENT_EVENTS, new PaymentRefundedEvent(
                event.getCorrelationId(), event.getOrderId(), event.getPaymentId()));
    }
}
