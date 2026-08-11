package com.openmind.order.contract.commands;

import com.openmind.order.contract.OrderCommand;

import java.util.UUID;

public record CancelOrderCommand(
        UUID correlationId,
        UUID orderId,
        String reason) implements OrderCommand {

    @Override
    public UUID getCorrelationId() {
        return correlationId;
    }
}
