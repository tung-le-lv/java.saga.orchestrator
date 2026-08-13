package com.openmind.order.application.integrationcommandhandlers;

import com.openmind.order.contract.commands.MarkOrderAsPaymentFailedCommand;
import com.openmind.shared.messaging.IntegrationMessageHandler;
import org.axonframework.commandhandling.CommandExecutionException;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class MarkOrderAsPaymentFailedCommandConsumer implements IntegrationMessageHandler<MarkOrderAsPaymentFailedCommand> {

    private static final Logger log = LoggerFactory.getLogger(MarkOrderAsPaymentFailedCommandConsumer.class);

    private final CommandGateway commandGateway;

    public MarkOrderAsPaymentFailedCommandConsumer(CommandGateway commandGateway) {
        this.commandGateway = commandGateway;
    }

    @Override
    public void handle(MarkOrderAsPaymentFailedCommand message) {
        try {
            commandGateway.sendAndWait(new com.openmind.order.application.commands.markorderaspaymentfailed.MarkOrderAsPaymentFailedCommand(
                    message.orderId(), message.reason(), message.correlationId()));
        } catch (CommandExecutionException e) {
            log.warn("[Order] MarkOrderAsPaymentFailed failed - OrderId: {}, Reason: {}", message.orderId(), causeMessage(e));
        }
    }

    private String causeMessage(CommandExecutionException e) {
        return e.getCause() != null ? e.getCause().getMessage() : e.getMessage();
    }
}
