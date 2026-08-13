package com.openmind.order.application.commands.markorderasbackordered;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record MarkOrderAsBackOrderedCommand(
        @NotNull UUID orderId,
        String reason,
        UUID correlationId) {
}
