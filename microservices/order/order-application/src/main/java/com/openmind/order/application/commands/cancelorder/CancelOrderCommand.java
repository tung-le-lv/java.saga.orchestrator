package com.openmind.order.application.commands.cancelorder;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CancelOrderCommand(
        @NotNull UUID orderId,
        String reason,
        UUID correlationId) {
}
