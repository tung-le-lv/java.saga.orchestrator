package com.openmind.email.api.handlers;

import com.openmind.email.contract.commands.SendPaymentFailedEmailCommand;
import com.openmind.shared.messaging.IntegrationMessageHandler;
import org.springframework.stereotype.Component;

@Component
public class SendPaymentFailedEmailHandler implements IntegrationMessageHandler<SendPaymentFailedEmailCommand> {

    private final EmailSender emailSender;

    public SendPaymentFailedEmailHandler(EmailSender emailSender) {
        this.emailSender = emailSender;
    }

    @Override
    public void handle(SendPaymentFailedEmailCommand message) {
        emailSender.send(message.correlationId(), message.orderId(), "PaymentFailed", message.customerEmail());
    }
}
