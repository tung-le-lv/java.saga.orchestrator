package com.openmind.order.application.integrationcommandhandlers;

import com.openmind.order.contract.commands.MarkOrderAsShippedCommand;
import com.openmind.shared.application.commands.CommandBus;
import com.openmind.shared.messaging.IntegrationMessageHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class MarkOrderAsShippedCommandConsumer implements IntegrationMessageHandler<MarkOrderAsShippedCommand> {

    private static final Logger log = LoggerFactory.getLogger(MarkOrderAsShippedCommandConsumer.class);

    private final CommandBus commandBus;

    public MarkOrderAsShippedCommandConsumer(CommandBus commandBus) {
        this.commandBus = commandBus;
    }

    @Override
    public void handle(MarkOrderAsShippedCommand message) {
        var result = commandBus.send(new com.openmind.order.application.commands.markorderasshipped.MarkOrderAsShippedCommand(
                message.orderId(), message.trackingNumber(), message.correlationId()));

        if (!result.isSuccess()) {
            log.warn("[Order] MarkOrderAsShipped failed - OrderId: {}, Reason: {}", message.orderId(), result.getErrorMessage());
        }
    }
}
