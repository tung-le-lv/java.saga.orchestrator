package com.openmind.order.application.commands.markorderaspaymentfailed;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record MarkOrderAsPaymentFailedCommand(
        @NotNull UUID orderId,
        String reason,
        UUID correlationId) {
}
