package com.openmind.order.contract.commands;

import com.openmind.order.contract.OrderCommand;

import java.util.UUID;

public record MarkOrderAsShippedCommand(
        UUID correlationId,
        UUID orderId,
        String trackingNumber) implements OrderCommand {

    @Override
    public UUID getCorrelationId() {
        return correlationId;
    }
}
