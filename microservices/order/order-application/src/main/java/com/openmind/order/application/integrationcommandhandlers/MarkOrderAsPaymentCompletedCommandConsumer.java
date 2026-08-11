package com.openmind.order.application.integrationcommandhandlers;

import com.openmind.order.contract.commands.MarkOrderAsPaymentCompletedCommand;
import com.openmind.shared.application.commands.CommandBus;
import com.openmind.shared.messaging.IntegrationMessageHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class MarkOrderAsPaymentCompletedCommandConsumer implements IntegrationMessageHandler<MarkOrderAsPaymentCompletedCommand> {

    private static final Logger log = LoggerFactory.getLogger(MarkOrderAsPaymentCompletedCommandConsumer.class);

    private final CommandBus commandBus;

    public MarkOrderAsPaymentCompletedCommandConsumer(CommandBus commandBus) {
        this.commandBus = commandBus;
    }

    @Override
    public void handle(MarkOrderAsPaymentCompletedCommand message) {
        var result = commandBus.send(new com.openmind.order.application.commands.markorderaspaymentcompleted.MarkOrderAsPaymentCompletedCommand(
                message.orderId(), message.transactionId(), message.correlationId()));

        if (!result.isSuccess()) {
            log.warn("[Order] MarkOrderAsPaymentCompleted failed - OrderId: {}, Reason: {}", message.orderId(), result.getErrorMessage());
        }
    }
}
