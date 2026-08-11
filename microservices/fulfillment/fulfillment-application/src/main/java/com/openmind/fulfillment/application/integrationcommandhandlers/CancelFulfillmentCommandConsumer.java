package com.openmind.fulfillment.application.integrationcommandhandlers;

import com.openmind.fulfillment.contract.commands.CancelFulfillmentCommand;
import com.openmind.shared.application.commands.CommandBus;
import com.openmind.shared.messaging.IntegrationMessageHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CancelFulfillmentCommandConsumer implements IntegrationMessageHandler<CancelFulfillmentCommand> {

    private static final Logger log = LoggerFactory.getLogger(CancelFulfillmentCommandConsumer.class);

    private final CommandBus commandBus;

    public CancelFulfillmentCommandConsumer(CommandBus commandBus) {
        this.commandBus = commandBus;
    }

    @Override
    public void handle(CancelFulfillmentCommand message) {
        var result = commandBus.send(new com.openmind.fulfillment.application.commands.cancelfulfillment.CancelFulfillmentCommand(
                message.orderId(), message.reason()));

        if (!result.isSuccess()) {
            log.warn("[Fulfillment] CancelFulfillment failed - OrderId: {}, Reason: {}", message.orderId(), result.getErrorMessage());
        }
    }
}
