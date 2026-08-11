package com.openmind.order.application.integrationcommandhandlers;

import com.openmind.order.contract.commands.MarkOrderAsBackOrderedCommand;
import com.openmind.shared.application.commands.CommandBus;
import com.openmind.shared.messaging.IntegrationMessageHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class MarkOrderAsBackOrderedCommandConsumer implements IntegrationMessageHandler<MarkOrderAsBackOrderedCommand> {

    private static final Logger log = LoggerFactory.getLogger(MarkOrderAsBackOrderedCommandConsumer.class);

    private final CommandBus commandBus;

    public MarkOrderAsBackOrderedCommandConsumer(CommandBus commandBus) {
        this.commandBus = commandBus;
    }

    @Override
    public void handle(MarkOrderAsBackOrderedCommand message) {
        var result = commandBus.send(new com.openmind.order.application.commands.markorderasbackordered.MarkOrderAsBackOrderedCommand(
                message.orderId(), message.reason(), message.correlationId()));

        if (!result.isSuccess()) {
            log.warn("[Order] MarkOrderAsBackOrdered failed - OrderId: {}, Reason: {}", message.orderId(), result.getErrorMessage());
        }
    }
}
