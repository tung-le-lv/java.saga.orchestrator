package com.openmind.fulfillment.contract.commands;

import com.openmind.fulfillment.contract.FulfillmentCommand;
import com.openmind.fulfillment.contract.FulfillmentItemDto;

import java.util.List;
import java.util.UUID;

public record FulfillOrderCommand(
        UUID correlationId,
        UUID orderId,
        UUID customerId,
        List<FulfillmentItemDto> items,
        String shippingAddress) implements FulfillmentCommand {

    @Override
    public UUID getCorrelationId() {
        return correlationId;
    }
}
