package com.openmind.order.application.commands.markorderaspaymentcompleted;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record MarkOrderAsPaymentCompletedCommand(
        @NotNull UUID orderId,
        String transactionId,
        UUID correlationId) {
}
