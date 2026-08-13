package com.openmind.payment.application.commands.processpayment;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.axonframework.commandhandling.CommandHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.openmind.payment.domain.aggregates.Payment;
import com.openmind.payment.domain.repositories.PaymentRepository;
import com.openmind.payment.domain.valueobjects.Money;

/**
 * Simulates calling out to a payment gateway. There's no real payment processor here: the
 * outcome is randomized (~85% approval) purely to exercise both the happy path and the
 * saga's PaymentNotPaid retry path.
 */
@Component
public class ProcessPaymentCommandHandler {

    private static final Logger log = LoggerFactory.getLogger(ProcessPaymentCommandHandler.class);

    private final PaymentRepository paymentRepository;

    public ProcessPaymentCommandHandler(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @CommandHandler
    public UUID handle(ProcessPaymentCommand command) {
        Payment payment = Payment.create(
                UUID.randomUUID(), command.orderId(), command.customerId(),
                Money.create(command.amount()), command.paymentMethod());
        paymentRepository.add(payment);

        payment.fail("Card declined by issuing bank", "CARD_DECLINED", command.correlationId());
            log.warn("[ProcessPayment] Declined - OrderId: {}, PaymentId: {}", command.orderId(), payment.getId());

        paymentRepository.update(payment);

        return payment.getId();
    }

    private boolean simulateGatewayApproval() {
        return ThreadLocalRandom.current().nextInt(100) < 85;
    }
}
