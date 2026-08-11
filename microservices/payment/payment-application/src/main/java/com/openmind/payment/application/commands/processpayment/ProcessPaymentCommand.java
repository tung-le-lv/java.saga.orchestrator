package com.openmind.payment.application.commands.processpayment;

import com.openmind.shared.application.commands.Command;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.UUID;

public record ProcessPaymentCommand(
        @NotNull UUID orderId,
        @NotNull UUID customerId,
        @Positive BigDecimal amount,
        String paymentMethod,
        String cardNumber,
        String cardExpiry,
        UUID correlationId) implements Command<UUID> {
}
