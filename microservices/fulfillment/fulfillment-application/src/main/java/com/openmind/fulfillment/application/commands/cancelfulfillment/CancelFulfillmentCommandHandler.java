package com.openmind.fulfillment.application.commands.cancelfulfillment;

import com.openmind.fulfillment.domain.repositories.FulfillmentRepository;
import com.openmind.shared.application.commands.CommandHandler;
import com.openmind.shared.application.commands.CommandResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CancelFulfillmentCommandHandler implements CommandHandler<CancelFulfillmentCommand, Void> {

    private static final Logger log = LoggerFactory.getLogger(CancelFulfillmentCommandHandler.class);

    private final FulfillmentRepository fulfillmentRepository;

    public CancelFulfillmentCommandHandler(FulfillmentRepository fulfillmentRepository) {
        this.fulfillmentRepository = fulfillmentRepository;
    }

    @Override
    public CommandResult<Void> handle(CancelFulfillmentCommand command) {
        try {
            return fulfillmentRepository.findByOrderId(command.orderId())
                    .map(fulfillment -> {
                        fulfillment.cancel(command.reason());
                        fulfillmentRepository.update(fulfillment);
                        return CommandResult.<Void>success(null);
                    })
                    .orElseGet(() -> CommandResult.failure("Fulfillment not found for order", "FULFILLMENT_NOT_FOUND"));
        } catch (Exception e) {
            log.error("[CancelFulfillment] ERROR: {}", e.getMessage(), e);
            return CommandResult.failure(e.getMessage(), "CANCEL_FULFILLMENT_FAILED");
        }
    }
}
