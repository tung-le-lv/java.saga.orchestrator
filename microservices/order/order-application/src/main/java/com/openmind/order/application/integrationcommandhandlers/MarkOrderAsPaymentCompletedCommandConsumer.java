package com.openmind.order.application.integrationcommandhandlers;

import com.openmind.order.contract.commands.MarkOrderAsPaymentCompletedCommand;
import com.openmind.shared.messaging.IntegrationMessageHandler;
import org.axonframework.commandhandling.CommandExecutionException;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class MarkOrderAsPaymentCompletedCommandConsumer implements IntegrationMessageHandler<MarkOrderAsPaymentCompletedCommand> {

    private static final Logger log = LoggerFactory.getLogger(MarkOrderAsPaymentCompletedCommandConsumer.class);

    private final CommandGateway commandGateway;

    public MarkOrderAsPaymentCompletedCommandConsumer(CommandGateway commandGateway) {
        this.commandGateway = commandGateway;
    }

    @Override
    public void handle(MarkOrderAsPaymentCompletedCommand message) {
        try {
            commandGateway.sendAndWait(new com.openmind.order.application.commands.markorderaspaymentcompleted.MarkOrderAsPaymentCompletedCommand(
                    message.orderId(), message.transactionId(), message.correlationId()));
        } catch (CommandExecutionException e) {
            log.warn("[Order] MarkOrderAsPaymentCompleted failed - OrderId: {}, Reason: {}", message.orderId(), causeMessage(e));
        }
    }

    private String causeMessage(CommandExecutionException e) {
        return e.getCause() != null ? e.getCause().getMessage() : e.getMessage();
    }
}
