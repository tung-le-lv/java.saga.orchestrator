package com.openmind.payment.application.commands.refundpayment;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record RefundPaymentCommand(
        @NotNull UUID orderId,
        UUID paymentId,
        String reason,
        UUID correlationId) {
}
