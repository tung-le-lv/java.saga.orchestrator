package com.openmind.email.api.handlers;

import com.openmind.email.contract.commands.SendOrderCancelledEmailCommand;
import com.openmind.shared.messaging.IntegrationMessageHandler;
import org.springframework.stereotype.Component;

@Component
public class SendOrderCancelledEmailHandler implements IntegrationMessageHandler<SendOrderCancelledEmailCommand> {

    private final EmailSender emailSender;

    public SendOrderCancelledEmailHandler(EmailSender emailSender) {
        this.emailSender = emailSender;
    }

    @Override
    public void handle(SendOrderCancelledEmailCommand message) {
        emailSender.send(message.correlationId(), message.orderId(), "OrderCancelled", message.customerEmail());
    }
}
