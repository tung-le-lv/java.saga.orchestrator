package com.openmind.fulfillment.application.commands.cancelfulfillment;

import com.openmind.fulfillment.domain.repositories.FulfillmentRepository;
import com.openmind.shared.domain.EntityNotFoundException;
import org.axonframework.commandhandling.CommandHandler;
import org.springframework.stereotype.Component;

@Component
public class CancelFulfillmentCommandHandler {

    private final FulfillmentRepository fulfillmentRepository;

    public CancelFulfillmentCommandHandler(FulfillmentRepository fulfillmentRepository) {
        this.fulfillmentRepository = fulfillmentRepository;
    }

    @CommandHandler
    public void handle(CancelFulfillmentCommand command) {
        var fulfillment = fulfillmentRepository.findByOrderId(command.orderId())
                .orElseThrow(() -> new EntityNotFoundException("Fulfillment not found for order"));
        fulfillment.cancel(command.reason());
        fulfillmentRepository.update(fulfillment);
    }
}
