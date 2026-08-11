package com.openmind.email.api.handlers;

import com.openmind.email.contract.commands.SendRefundEmailCommand;
import com.openmind.shared.messaging.IntegrationMessageHandler;
import org.springframework.stereotype.Component;

@Component
public class SendRefundEmailHandler implements IntegrationMessageHandler<SendRefundEmailCommand> {

    private final EmailSender emailSender;

    public SendRefundEmailHandler(EmailSender emailSender) {
        this.emailSender = emailSender;
    }

    @Override
    public void handle(SendRefundEmailCommand message) {
        emailSender.send(message.correlationId(), message.orderId(), "Refund", message.customerEmail());
    }
}
