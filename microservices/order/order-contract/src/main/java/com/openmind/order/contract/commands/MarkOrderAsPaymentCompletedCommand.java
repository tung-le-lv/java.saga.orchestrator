package com.openmind.order.contract.commands;

import com.openmind.order.contract.OrderCommand;

import java.util.UUID;

public record MarkOrderAsPaymentCompletedCommand(
        UUID correlationId,
        UUID orderId,
        String transactionId) implements OrderCommand {

    @Override
    public UUID getCorrelationId() {
        return correlationId;
    }
}
