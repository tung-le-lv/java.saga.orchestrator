package com.openmind.email.api.handlers;

import com.openmind.email.contract.commands.SendOrderConfirmationEmailCommand;
import com.openmind.shared.messaging.IntegrationMessageHandler;
import org.springframework.stereotype.Component;

@Component
public class SendOrderConfirmationEmailHandler implements IntegrationMessageHandler<SendOrderConfirmationEmailCommand> {

    private final EmailSender emailSender;

    public SendOrderConfirmationEmailHandler(EmailSender emailSender) {
        this.emailSender = emailSender;
    }

    @Override
    public void handle(SendOrderConfirmationEmailCommand message) {
        emailSender.send(message.correlationId(), message.orderId(), "OrderConfirmation", message.customerEmail());
    }
}
