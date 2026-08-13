package com.openmind.fulfillment.application.commands.fulfillorder;

import com.openmind.fulfillment.domain.aggregates.Fulfillment;
import com.openmind.fulfillment.domain.entities.FulfillmentItem;
import com.openmind.fulfillment.domain.repositories.FulfillmentRepository;
import org.axonframework.commandhandling.CommandHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Simulates a warehouse/stock check. There's no real inventory system here: the outcome is
 * randomized (~85% in stock) purely to exercise both the happy path and the saga's
 * out-of-stock/refund/backorder compensation path.
 */
@Component
public class FulfillOrderCommandHandler {

    private static final Logger log = LoggerFactory.getLogger(FulfillOrderCommandHandler.class);

    private final FulfillmentRepository fulfillmentRepository;

    public FulfillOrderCommandHandler(FulfillmentRepository fulfillmentRepository) {
        this.fulfillmentRepository = fulfillmentRepository;
    }

    @CommandHandler
    public UUID handle(FulfillOrderCommand command) {
        Fulfillment fulfillment = Fulfillment.create(
                UUID.randomUUID(), command.orderId(), command.customerId(), command.shippingAddress());

        for (FulfillmentItemCommand item : command.items()) {
            fulfillment.addItem(FulfillmentItem.create(item.productId(), item.productName(), item.quantity()));
        }

        fulfillmentRepository.add(fulfillment);

        if (simulateInStock()) {
            String trackingNumber = "TRK-" + UUID.randomUUID().toString().substring(0, 10).toUpperCase();
            Instant estimatedDelivery = Instant.now().plus(5, ChronoUnit.DAYS);
            fulfillment.ship(trackingNumber, estimatedDelivery, command.correlationId());
            log.info("[FulfillOrder] Shipped - OrderId: {}, FulfillmentId: {}, TrackingNumber: {}",
                    command.orderId(), fulfillment.getId(), trackingNumber);
        } else {
            fulfillment.fail("Items out of stock", command.correlationId());
            log.warn("[FulfillOrder] Out of stock - OrderId: {}, FulfillmentId: {}", command.orderId(), fulfillment.getId());
        }

        fulfillmentRepository.update(fulfillment);

        return fulfillment.getId();
    }

    private boolean simulateInStock() {
        return ThreadLocalRandom.current().nextInt(100) < 85;
    }
}
