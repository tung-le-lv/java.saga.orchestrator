package com.openmind.email.contract.commands;

import com.openmind.email.contract.EmailCommand;

import java.math.BigDecimal;
import java.util.UUID;

public record SendRefundEmailCommand(
        UUID correlationId,
        UUID orderId,
        UUID customerId,
        String customerEmail,
        String customerName,
        BigDecimal refundAmount) implements EmailCommand {

    @Override
    public UUID getCorrelationId() {
        return correlationId;
    }
}
