package com.openmind.payment.application.queries.getpayment;

import java.math.BigDecimal;
import java.util.UUID;

public record PaymentDto(
        UUID id,
        UUID orderId,
        UUID customerId,
        BigDecimal amount,
        String status,
        String transactionId,
        String failureReason) {
}
