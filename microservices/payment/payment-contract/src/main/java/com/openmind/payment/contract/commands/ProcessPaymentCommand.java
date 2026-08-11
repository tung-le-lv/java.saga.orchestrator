package com.openmind.payment.contract.commands;

import com.openmind.payment.contract.PaymentCommand;

import java.math.BigDecimal;
import java.util.UUID;

public record ProcessPaymentCommand(
        UUID correlationId,
        UUID orderId,
        UUID customerId,
        BigDecimal amount,
        String paymentMethod,
        String cardNumber,
        String cardExpiry) implements PaymentCommand {

    @Override
    public UUID getCorrelationId() {
        return correlationId;
    }
}
