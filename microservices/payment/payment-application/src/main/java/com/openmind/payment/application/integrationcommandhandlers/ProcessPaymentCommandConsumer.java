package com.openmind.payment.application.integrationcommandhandlers;

import com.openmind.payment.contract.commands.ProcessPaymentCommand;
import com.openmind.shared.messaging.IntegrationMessageHandler;
import org.axonframework.commandhandling.CommandExecutionException;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ProcessPaymentCommandConsumer implements IntegrationMessageHandler<ProcessPaymentCommand> {

    private static final Logger log = LoggerFactory.getLogger(ProcessPaymentCommandConsumer.class);

    private final CommandGateway commandGateway;

    public ProcessPaymentCommandConsumer(CommandGateway commandGateway) {
        this.commandGateway = commandGateway;
    }

    @Override
    public void handle(ProcessPaymentCommand message) {
        try {
            commandGateway.sendAndWait(new com.openmind.payment.application.commands.processpayment.ProcessPaymentCommand(
                    message.orderId(), message.customerId(), message.amount(), message.paymentMethod(),
                    message.cardNumber(), message.cardExpiry(), message.correlationId()));
        } catch (CommandExecutionException e) {
            log.warn("[Payment] ProcessPayment failed - OrderId: {}, Reason: {}", message.orderId(), causeMessage(e));
        }
    }

    private String causeMessage(CommandExecutionException e) {
        return e.getCause() != null ? e.getCause().getMessage() : e.getMessage();
    }
}
