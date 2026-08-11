package com.openmind.payment.application.commands.retrypayment;

import com.openmind.payment.domain.repositories.PaymentRepository;
import com.openmind.shared.application.commands.CommandHandler;
import com.openmind.shared.application.commands.CommandResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class RetryPaymentCommandHandler implements CommandHandler<RetryPaymentCommand, Void> {

    private static final Logger log = LoggerFactory.getLogger(RetryPaymentCommandHandler.class);

    private final PaymentRepository paymentRepository;

    public RetryPaymentCommandHandler(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Override
    public CommandResult<Void> handle(RetryPaymentCommand command) {
        try {
            return paymentRepository.findByOrderId(command.orderId())
                    .map(payment -> {
                        // The saga's correlation id is the order id for the lifetime of the saga instance.
                        UUID correlationId = command.orderId();
                        String transactionId = "TXN-RETRY-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
                        payment.complete(transactionId, correlationId);
                        paymentRepository.update(payment);
                        log.info("[RetryPayment] Forced completion - OrderId: {}, PaymentId: {}", command.orderId(), payment.getId());
                        return CommandResult.<Void>success(null);
                    })
                    .orElseGet(() -> CommandResult.failure("No payment found for order", "PAYMENT_NOT_FOUND"));
        } catch (Exception e) {
            log.error("[RetryPayment] ERROR: {}", e.getMessage(), e);
            return CommandResult.failure(e.getMessage(), "RETRY_PAYMENT_FAILED");
        }
    }
}
