package com.openmind.payment.application.commands.retrypayment;

import com.openmind.shared.application.commands.Command;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/**
 * Forces the latest (failed) payment attempt for an order to succeed. Backs the manual
 * "retry via Payment API" path the orchestrator's PaymentNotPaid state waits on.
 */
public record RetryPaymentCommand(@NotNull UUID orderId) implements Command<Void> {
}
