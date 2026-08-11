package com.openmind.fulfillment.application.integrationcommandhandlers;

import com.openmind.fulfillment.application.commands.fulfillorder.FulfillmentItemCommand;
import com.openmind.fulfillment.contract.FulfillmentItemDto;
import com.openmind.fulfillment.contract.commands.FulfillOrderCommand;
import com.openmind.shared.application.commands.CommandBus;
import com.openmind.shared.messaging.IntegrationMessageHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class FulfillOrderCommandConsumer implements IntegrationMessageHandler<FulfillOrderCommand> {

    private static final Logger log = LoggerFactory.getLogger(FulfillOrderCommandConsumer.class);

    private final CommandBus commandBus;

    public FulfillOrderCommandConsumer(CommandBus commandBus) {
        this.commandBus = commandBus;
    }

    @Override
    public void handle(FulfillOrderCommand message) {
        var items = message.items().stream()
                .map(this::toItemCommand)
                .toList();

        var result = commandBus.send(new com.openmind.fulfillment.application.commands.fulfillorder.FulfillOrderCommand(
                message.orderId(), message.customerId(), items, message.shippingAddress(), message.correlationId()));

        if (!result.isSuccess()) {
            log.warn("[Fulfillment] FulfillOrder failed - OrderId: {}, Reason: {}", message.orderId(), result.getErrorMessage());
        }
    }

    private FulfillmentItemCommand toItemCommand(FulfillmentItemDto dto) {
        return new FulfillmentItemCommand(dto.productId(), dto.productName(), dto.quantity());
    }
}
