package com.openmind.fulfillment.application.integrationcommandhandlers;

import com.openmind.fulfillment.application.commands.fulfillorder.FulfillmentItemCommand;
import com.openmind.fulfillment.contract.FulfillmentItemDto;
import com.openmind.fulfillment.contract.commands.FulfillOrderCommand;
import com.openmind.shared.messaging.IntegrationMessageHandler;
import org.axonframework.commandhandling.CommandExecutionException;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class FulfillOrderCommandConsumer implements IntegrationMessageHandler<FulfillOrderCommand> {

    private static final Logger log = LoggerFactory.getLogger(FulfillOrderCommandConsumer.class);

    private final CommandGateway commandGateway;

    public FulfillOrderCommandConsumer(CommandGateway commandGateway) {
        this.commandGateway = commandGateway;
    }

    @Override
    public void handle(FulfillOrderCommand message) {
        var items = message.items().stream()
                .map(this::toItemCommand)
                .toList();

        try {
            commandGateway.sendAndWait(new com.openmind.fulfillment.application.commands.fulfillorder.FulfillOrderCommand(
                    message.orderId(), message.customerId(), items, message.shippingAddress(), message.correlationId()));
        } catch (CommandExecutionException e) {
            log.warn("[Fulfillment] FulfillOrder failed - OrderId: {}, Reason: {}", message.orderId(), causeMessage(e));
        }
    }

    private FulfillmentItemCommand toItemCommand(FulfillmentItemDto dto) {
        return new FulfillmentItemCommand(dto.productId(), dto.productName(), dto.quantity());
    }

    private String causeMessage(CommandExecutionException e) {
        return e.getCause() != null ? e.getCause().getMessage() : e.getMessage();
    }
}
