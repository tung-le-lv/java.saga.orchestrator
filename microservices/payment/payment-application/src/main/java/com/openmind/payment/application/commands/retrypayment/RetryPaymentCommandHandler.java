package com.openmind.payment.application.commands.retrypayment;

import com.openmind.payment.domain.repositories.PaymentRepository;
import com.openmind.shared.domain.EntityNotFoundException;
import org.axonframework.commandhandling.CommandHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class RetryPaymentCommandHandler {

    private static final Logger log = LoggerFactory.getLogger(RetryPaymentCommandHandler.class);

    private final PaymentRepository paymentRepository;

    public RetryPaymentCommandHandler(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @CommandHandler
    public void handle(RetryPaymentCommand command) {
        var payment = paymentRepository.findByOrderId(command.orderId())
                .orElseThrow(() -> new EntityNotFoundException("No payment found for order"));

        UUID correlationId = command.orderId();

        // Simulate a successful payment completion
        String transactionId = "TXN-RETRY-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        
        payment.complete(transactionId, correlationId);
        paymentRepository.update(payment);
        
        log.info("[RetryPayment] Forced completion - OrderId: {}, PaymentId: {}", command.orderId(), payment.getId());
    }
}
