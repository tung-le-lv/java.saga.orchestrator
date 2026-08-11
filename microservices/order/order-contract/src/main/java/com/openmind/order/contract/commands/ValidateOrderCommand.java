package com.openmind.order.contract.commands;

import com.openmind.order.contract.OrderCommand;

import java.util.UUID;

/**
 * Asks the Order service to confirm an order exists and return its details.
 */
public record ValidateOrderCommand(UUID correlationId, UUID orderId) implements OrderCommand {

    @Override
    public UUID getCorrelationId() {
        return correlationId;
    }
}
