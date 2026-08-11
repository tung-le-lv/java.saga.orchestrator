package com.openmind.email.contract.commands;

import com.openmind.email.contract.EmailCommand;

import java.util.UUID;

public record SendOrderCancelledEmailCommand(
        UUID correlationId,
        UUID orderId,
        UUID customerId,
        String customerEmail,
        String customerName,
        String reason) implements EmailCommand {

    @Override
    public UUID getCorrelationId() {
        return correlationId;
    }
}
