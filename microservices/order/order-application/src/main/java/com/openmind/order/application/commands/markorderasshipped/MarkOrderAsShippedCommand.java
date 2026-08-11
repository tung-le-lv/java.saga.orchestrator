package com.openmind.order.application.commands.markorderasshipped;

import com.openmind.shared.application.commands.Command;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record MarkOrderAsShippedCommand(
        @NotNull UUID orderId,
        String trackingNumber,
        UUID correlationId) implements Command<Void> {
}
