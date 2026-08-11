package com.openmind.order.application.commands.cancelorder;

import com.openmind.shared.application.commands.Command;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CancelOrderCommand(
        @NotNull UUID orderId,
        String reason,
        UUID correlationId) implements Command<Void> {
}
