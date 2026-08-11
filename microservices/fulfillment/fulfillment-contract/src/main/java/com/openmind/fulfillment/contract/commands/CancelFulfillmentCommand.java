package com.openmind.fulfillment.contract.commands;

import com.openmind.fulfillment.contract.FulfillmentCommand;

import java.util.UUID;

public record CancelFulfillmentCommand(
        UUID correlationId,
        UUID orderId,
        String reason) implements FulfillmentCommand {

    @Override
    public UUID getCorrelationId() {
        return correlationId;
    }
}
