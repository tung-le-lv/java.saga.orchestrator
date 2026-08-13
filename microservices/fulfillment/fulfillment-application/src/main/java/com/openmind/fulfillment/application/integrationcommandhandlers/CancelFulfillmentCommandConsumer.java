package com.openmind.fulfillment.application.integrationcommandhandlers;

import com.openmind.fulfillment.contract.commands.CancelFulfillmentCommand;
import com.openmind.shared.messaging.IntegrationMessageHandler;
import org.axonframework.commandhandling.CommandExecutionException;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CancelFulfillmentCommandConsumer implements IntegrationMessageHandler<CancelFulfillmentCommand> {

    private static final Logger log = LoggerFactory.getLogger(CancelFulfillmentCommandConsumer.class);

    private final CommandGateway commandGateway;

    public CancelFulfillmentCommandConsumer(CommandGateway commandGateway) {
        this.commandGateway = commandGateway;
    }

    @Override
    public void handle(CancelFulfillmentCommand message) {
        try {
            commandGateway.sendAndWait(new com.openmind.fulfillment.application.commands.cancelfulfillment.CancelFulfillmentCommand(
                    message.orderId(), message.reason()));
        } catch (CommandExecutionException e) {
            log.warn("[Fulfillment] CancelFulfillment failed - OrderId: {}, Reason: {}", message.orderId(), causeMessage(e));
        }
    }

    private String causeMessage(CommandExecutionException e) {
        return e.getCause() != null ? e.getCause().getMessage() : e.getMessage();
    }
}
