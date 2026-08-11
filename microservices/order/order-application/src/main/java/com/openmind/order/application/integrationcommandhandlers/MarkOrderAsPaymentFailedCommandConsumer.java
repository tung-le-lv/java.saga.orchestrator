package com.openmind.order.application.integrationcommandhandlers;

import com.openmind.order.contract.commands.MarkOrderAsPaymentFailedCommand;
import com.openmind.shared.application.commands.CommandBus;
import com.openmind.shared.messaging.IntegrationMessageHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class MarkOrderAsPaymentFailedCommandConsumer implements IntegrationMessageHandler<MarkOrderAsPaymentFailedCommand> {

    private static final Logger log = LoggerFactory.getLogger(MarkOrderAsPaymentFailedCommandConsumer.class);

    private final CommandBus commandBus;

    public MarkOrderAsPaymentFailedCommandConsumer(CommandBus commandBus) {
        this.commandBus = commandBus;
    }

    @Override
    public void handle(MarkOrderAsPaymentFailedCommand message) {
        var result = commandBus.send(new com.openmind.order.application.commands.markorderaspaymentfailed.MarkOrderAsPaymentFailedCommand(
                message.orderId(), message.reason(), message.correlationId()));

        if (!result.isSuccess()) {
            log.warn("[Order] MarkOrderAsPaymentFailed failed - OrderId: {}, Reason: {}", message.orderId(), result.getErrorMessage());
        }
    }
}
