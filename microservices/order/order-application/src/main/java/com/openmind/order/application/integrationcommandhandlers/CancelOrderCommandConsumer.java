package com.openmind.order.application.integrationcommandhandlers;

import com.openmind.order.contract.commands.CancelOrderCommand;
import com.openmind.shared.application.commands.CommandBus;
import com.openmind.shared.messaging.IntegrationMessageHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CancelOrderCommandConsumer implements IntegrationMessageHandler<CancelOrderCommand> {

    private static final Logger log = LoggerFactory.getLogger(CancelOrderCommandConsumer.class);

    private final CommandBus commandBus;

    public CancelOrderCommandConsumer(CommandBus commandBus) {
        this.commandBus = commandBus;
    }

    @Override
    public void handle(CancelOrderCommand message) {
        var result = commandBus.send(new com.openmind.order.application.commands.cancelorder.CancelOrderCommand(
                message.orderId(), message.reason(), message.correlationId()));

        if (!result.isSuccess()) {
            log.warn("[Order] CancelOrder failed - OrderId: {}, Reason: {}", message.orderId(), result.getErrorMessage());
        }
    }
}
