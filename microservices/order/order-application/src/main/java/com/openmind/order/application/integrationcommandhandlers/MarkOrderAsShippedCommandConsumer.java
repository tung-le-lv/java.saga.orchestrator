package com.openmind.order.application.integrationcommandhandlers;

import com.openmind.order.contract.commands.MarkOrderAsShippedCommand;
import com.openmind.shared.messaging.IntegrationMessageHandler;
import org.axonframework.commandhandling.CommandExecutionException;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class MarkOrderAsShippedCommandConsumer implements IntegrationMessageHandler<MarkOrderAsShippedCommand> {

    private static final Logger log = LoggerFactory.getLogger(MarkOrderAsShippedCommandConsumer.class);

    private final CommandGateway commandGateway;

    public MarkOrderAsShippedCommandConsumer(CommandGateway commandGateway) {
        this.commandGateway = commandGateway;
    }

    @Override
    public void handle(MarkOrderAsShippedCommand message) {
        try {
            commandGateway.sendAndWait(new com.openmind.order.application.commands.markorderasshipped.MarkOrderAsShippedCommand(
                    message.orderId(), message.trackingNumber(), message.correlationId()));
        } catch (CommandExecutionException e) {
            log.warn("[Order] MarkOrderAsShipped failed - OrderId: {}, Reason: {}", message.orderId(), causeMessage(e));
        }
    }

    private String causeMessage(CommandExecutionException e) {
        return e.getCause() != null ? e.getCause().getMessage() : e.getMessage();
    }
}
