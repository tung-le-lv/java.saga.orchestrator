package com.openmind.order.application.commands.markorderaspaymentcompleted;

import com.openmind.shared.application.commands.Command;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record MarkOrderAsPaymentCompletedCommand(
        @NotNull UUID orderId,
        String transactionId,
        UUID correlationId) implements Command<Void> {
}
