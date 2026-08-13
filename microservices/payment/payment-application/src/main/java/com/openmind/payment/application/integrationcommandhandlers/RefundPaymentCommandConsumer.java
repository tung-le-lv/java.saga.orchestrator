package com.openmind.payment.application.integrationcommandhandlers;

import com.openmind.payment.contract.commands.RefundPaymentCommand;
import com.openmind.shared.messaging.IntegrationMessageHandler;
import org.axonframework.commandhandling.CommandExecutionException;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class RefundPaymentCommandConsumer implements IntegrationMessageHandler<RefundPaymentCommand> {

    private static final Logger log = LoggerFactory.getLogger(RefundPaymentCommandConsumer.class);

    private final CommandGateway commandGateway;

    public RefundPaymentCommandConsumer(CommandGateway commandGateway) {
        this.commandGateway = commandGateway;
    }

    @Override
    public void handle(RefundPaymentCommand message) {
        try {
            commandGateway.sendAndWait(new com.openmind.payment.application.commands.refundpayment.RefundPaymentCommand(
                    message.orderId(), message.paymentId(), message.reason(), message.correlationId()));
        } catch (CommandExecutionException e) {
            log.warn("[Payment] RefundPayment failed - OrderId: {}, Reason: {}", message.orderId(), causeMessage(e));
        }
    }

    private String causeMessage(CommandExecutionException e) {
        return e.getCause() != null ? e.getCause().getMessage() : e.getMessage();
    }
}
