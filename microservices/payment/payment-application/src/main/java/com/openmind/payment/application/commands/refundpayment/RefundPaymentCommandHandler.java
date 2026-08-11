package com.openmind.payment.application.commands.refundpayment;

import com.openmind.payment.domain.repositories.PaymentRepository;
import com.openmind.shared.application.commands.CommandHandler;
import com.openmind.shared.application.commands.CommandResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class RefundPaymentCommandHandler implements CommandHandler<RefundPaymentCommand, Void> {

    private static final Logger log = LoggerFactory.getLogger(RefundPaymentCommandHandler.class);

    private final PaymentRepository paymentRepository;

    public RefundPaymentCommandHandler(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Override
    public CommandResult<Void> handle(RefundPaymentCommand command) {
        try {
            return paymentRepository.findByOrderId(command.orderId())
                    .map(payment -> {
                        payment.refund(command.correlationId());
                        paymentRepository.update(payment);
                        return CommandResult.<Void>success(null);
                    })
                    .orElseGet(() -> CommandResult.failure("Payment not found for order", "PAYMENT_NOT_FOUND"));
        } catch (Exception e) {
            log.error("[RefundPayment] ERROR: {}", e.getMessage(), e);
            return CommandResult.failure(e.getMessage(), "REFUND_PAYMENT_FAILED");
        }
    }
}
