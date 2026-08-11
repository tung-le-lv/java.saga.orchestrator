package com.openmind.payment.contract.commands;

import com.openmind.payment.contract.PaymentCommand;

import java.math.BigDecimal;
import java.util.UUID;

public record RefundPaymentCommand(
        UUID correlationId,
        UUID orderId,
        UUID paymentId,
        BigDecimal amount,
        String reason) implements PaymentCommand {

    @Override
    public UUID getCorrelationId() {
        return correlationId;
    }
}
