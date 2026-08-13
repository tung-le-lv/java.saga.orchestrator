package com.openmind.payment.application.commands.refundpayment;

import com.openmind.payment.domain.repositories.PaymentRepository;
import com.openmind.shared.domain.EntityNotFoundException;
import org.axonframework.commandhandling.CommandHandler;
import org.springframework.stereotype.Component;

@Component
public class RefundPaymentCommandHandler {

    private final PaymentRepository paymentRepository;

    public RefundPaymentCommandHandler(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @CommandHandler
    public void handle(RefundPaymentCommand command) {
        var payment = paymentRepository.findByOrderId(command.orderId())
                .orElseThrow(() -> new EntityNotFoundException("Payment not found for order"));
        payment.refund(command.correlationId());
        paymentRepository.update(payment);
    }
}
