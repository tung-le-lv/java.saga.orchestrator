package com.openmind.order.application.integrationcommandhandlers;

import com.openmind.order.contract.commands.MarkOrderAsBackOrderedCommand;
import com.openmind.shared.messaging.IntegrationMessageHandler;
import org.axonframework.commandhandling.CommandExecutionException;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class MarkOrderAsBackOrderedCommandConsumer implements IntegrationMessageHandler<MarkOrderAsBackOrderedCommand> {

    private static final Logger log = LoggerFactory.getLogger(MarkOrderAsBackOrderedCommandConsumer.class);

    private final CommandGateway commandGateway;

    public MarkOrderAsBackOrderedCommandConsumer(CommandGateway commandGateway) {
        this.commandGateway = commandGateway;
    }

    @Override
    public void handle(MarkOrderAsBackOrderedCommand message) {
        try {
            commandGateway.sendAndWait(new com.openmind.order.application.commands.markorderasbackordered.MarkOrderAsBackOrderedCommand(
                    message.orderId(), message.reason(), message.correlationId()));
        } catch (CommandExecutionException e) {
            log.warn("[Order] MarkOrderAsBackOrdered failed - OrderId: {}, Reason: {}", message.orderId(), causeMessage(e));
        }
    }

    private String causeMessage(CommandExecutionException e) {
        return e.getCause() != null ? e.getCause().getMessage() : e.getMessage();
    }
}
