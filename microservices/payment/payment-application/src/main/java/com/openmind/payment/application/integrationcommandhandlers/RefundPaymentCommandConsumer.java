package com.openmind.payment.application.integrationcommandhandlers;

import com.openmind.payment.contract.commands.RefundPaymentCommand;
import com.openmind.shared.application.commands.CommandBus;
import com.openmind.shared.messaging.IntegrationMessageHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class RefundPaymentCommandConsumer implements IntegrationMessageHandler<RefundPaymentCommand> {

    private static final Logger log = LoggerFactory.getLogger(RefundPaymentCommandConsumer.class);

    private final CommandBus commandBus;

    public RefundPaymentCommandConsumer(CommandBus commandBus) {
        this.commandBus = commandBus;
    }

    @Override
    public void handle(RefundPaymentCommand message) {
        var result = commandBus.send(new com.openmind.payment.application.commands.refundpayment.RefundPaymentCommand(
                message.orderId(), message.paymentId(), message.reason(), message.correlationId()));

        if (!result.isSuccess()) {
            log.warn("[Payment] RefundPayment failed - OrderId: {}, Reason: {}", message.orderId(), result.getErrorMessage());
        }
    }
}
