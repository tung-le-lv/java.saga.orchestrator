package com.openmind.email.contract.commands;

import com.openmind.email.contract.EmailCommand;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record SendBackorderEmailCommand(
        UUID correlationId,
        UUID orderId,
        UUID customerId,
        String customerEmail,
        String customerName,
        List<String> backorderedProducts,
        Instant estimatedAvailability) implements EmailCommand {

    @Override
    public UUID getCorrelationId() {
        return correlationId;
    }
}
