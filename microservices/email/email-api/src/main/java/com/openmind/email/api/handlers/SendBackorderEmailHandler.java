package com.openmind.email.api.handlers;

import com.openmind.email.contract.commands.SendBackorderEmailCommand;
import com.openmind.shared.messaging.IntegrationMessageHandler;
import org.springframework.stereotype.Component;

@Component
public class SendBackorderEmailHandler implements IntegrationMessageHandler<SendBackorderEmailCommand> {

    private final EmailSender emailSender;

    public SendBackorderEmailHandler(EmailSender emailSender) {
        this.emailSender = emailSender;
    }

    @Override
    public void handle(SendBackorderEmailCommand message) {
        emailSender.send(message.correlationId(), message.orderId(), "Backorder", message.customerEmail());
    }
}
