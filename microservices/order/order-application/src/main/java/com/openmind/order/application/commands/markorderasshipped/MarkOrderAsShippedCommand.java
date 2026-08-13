package com.openmind.order.application.commands.markorderasshipped;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record MarkOrderAsShippedCommand(
        @NotNull UUID orderId,
        String trackingNumber,
        UUID correlationId) {
}
