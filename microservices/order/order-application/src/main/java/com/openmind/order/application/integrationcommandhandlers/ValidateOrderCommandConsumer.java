package com.openmind.order.application.integrationcommandhandlers;

import com.openmind.order.application.queries.getorder.GetOrderQuery;
import com.openmind.order.application.queries.getorder.OrderDto;
import com.openmind.order.contract.commands.ValidateOrderCommand;
import com.openmind.order.contract.events.OrderValidatedEvent;
import com.openmind.order.contract.events.OrderValidationFailedEvent;
import com.openmind.shared.messaging.IntegrationMessageHandler;
import com.openmind.shared.messaging.MessagePublisher;
import com.openmind.shared.messaging.Topics;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Consumer for {@link ValidateOrderCommand} from the orchestrator.
 * Validates that an order exists and returns its details.
 */
@Component
public class ValidateOrderCommandConsumer implements IntegrationMessageHandler<ValidateOrderCommand> {

    private static final Logger log = LoggerFactory.getLogger(ValidateOrderCommandConsumer.class);

    private final QueryGateway queryGateway;
    private final MessagePublisher messagePublisher;

    public ValidateOrderCommandConsumer(QueryGateway queryGateway, MessagePublisher messagePublisher) {
        this.queryGateway = queryGateway;
        this.messagePublisher = messagePublisher;
    }

    @Override
    public void handle(ValidateOrderCommand message) {
        OrderDto order = queryGateway.query(new GetOrderQuery(message.orderId()), ResponseTypes.instanceOf(OrderDto.class)).join();
        var correlationId = message.correlationId();

        if (order != null) {
            messagePublisher.publish(Topics.ORDER_EVENTS, new OrderValidatedEvent(
                    correlationId,
                    order.id(),
                    order.customerId(),
                    order.totalAmount(),
                    order.shippingAddress(),
                    "customer-" + order.customerId() + "@example.com",
                    "Customer " + order.customerId().toString().substring(0, 8),
                    order.items()));

            log.info("[Order] Published OrderValidatedEvent - OrderId: {}, CorrelationId: {}", order.id(), correlationId);
        } else {
            String reason = "Order not found";

            messagePublisher.publish(Topics.ORDER_EVENTS, new OrderValidationFailedEvent(
                    correlationId, message.orderId(), reason));

            log.warn("[Order] Published OrderValidationFailedEvent - OrderId: {}, Reason: {}, CorrelationId: {}",
                    message.orderId(), reason, correlationId);
        }
    }
}
