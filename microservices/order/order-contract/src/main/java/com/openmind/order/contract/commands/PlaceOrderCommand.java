package com.openmind.order.contract.commands;

import com.openmind.order.contract.OrderCommand;

import java.util.UUID;

/**
 * Starts the order placement saga in the Orchestrator for an already-created order.
 */
public record PlaceOrderCommand(UUID correlationId, UUID orderId) implements OrderCommand {

    @Override
    public UUID getCorrelationId() {
        return correlationId;
    }
}
