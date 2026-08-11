package com.openmind.payment.application.queries.getpayment;

import com.openmind.shared.application.queries.Query;

import java.util.UUID;

public record GetPaymentQuery(UUID orderId) implements Query<PaymentDto> {
}
