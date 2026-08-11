package com.openmind.payment.application.integrationcommandhandlers;

import com.openmind.payment.contract.commands.ProcessPaymentCommand;
import com.openmind.shared.application.commands.CommandBus;
import com.openmind.shared.messaging.IntegrationMessageHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ProcessPaymentCommandConsumer implements IntegrationMessageHandler<ProcessPaymentCommand> {

    private static final Logger log = LoggerFactory.getLogger(ProcessPaymentCommandConsumer.class);

    private final CommandBus commandBus;

    public ProcessPaymentCommandConsumer(CommandBus commandBus) {
        this.commandBus = commandBus;
    }

    @Override
    public void handle(ProcessPaymentCommand message) {
        var result = commandBus.send(new com.openmind.payment.application.commands.processpayment.ProcessPaymentCommand(
                message.orderId(), message.customerId(), message.amount(), message.paymentMethod(),
                message.cardNumber(), message.cardExpiry(), message.correlationId()));

        if (!result.isSuccess()) {
            log.warn("[Payment] ProcessPayment failed - OrderId: {}, Reason: {}", message.orderId(), result.getErrorMessage());
        }
    }
}
