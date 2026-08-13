package com.openmind.orchestrator.api.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.openmind.email.contract.events.EmailFailedEvent;
import com.openmind.email.contract.events.EmailSentEvent;
import com.openmind.fulfillment.contract.events.FulfillmentFailedEvent;
import com.openmind.fulfillment.contract.events.OrderShippedEvent;
import com.openmind.order.contract.commands.PlaceOrderCommand;
import com.openmind.order.contract.events.OrderValidatedEvent;
import com.openmind.order.contract.events.OrderValidationFailedEvent;
import com.openmind.payment.contract.events.PaymentCompletedEvent;
import com.openmind.payment.contract.events.PaymentFailedEvent;
import com.openmind.payment.contract.events.PaymentRefundedEvent;
import org.axonframework.eventhandling.gateway.EventGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Bridges raw SQS message bodies (our own {messageType, payload} envelope, see
 * {@code shared-messaging}) onto Axon's local event bus, so {@link
 * com.openmind.orchestrator.api.saga.OrderPlacementSaga} can react to them via
 * {@code @SagaEventHandler}. Unlike {@code MessageDispatcher} (which routes to a Spring bean
 * per message type), every recognized message here is simply published onto the event bus -
 * Axon itself routes it to the right saga instance by association.
 */
@Component
public class AxonBridgeDispatcher {

    private static final Logger log = LoggerFactory.getLogger(AxonBridgeDispatcher.class);

    private static final Map<String, Class<?>> MESSAGE_TYPES = Map.ofEntries(
            Map.entry("PlaceOrderCommand", PlaceOrderCommand.class),
            Map.entry("OrderValidatedEvent", OrderValidatedEvent.class),
            Map.entry("OrderValidationFailedEvent", OrderValidationFailedEvent.class),
            Map.entry("PaymentCompletedEvent", PaymentCompletedEvent.class),
            Map.entry("PaymentFailedEvent", PaymentFailedEvent.class),
            Map.entry("PaymentRefundedEvent", PaymentRefundedEvent.class),
            Map.entry("OrderShippedEvent", OrderShippedEvent.class),
            Map.entry("FulfillmentFailedEvent", FulfillmentFailedEvent.class),
            Map.entry("EmailSentEvent", EmailSentEvent.class),
            Map.entry("EmailFailedEvent", EmailFailedEvent.class));

    private final ObjectMapper objectMapper;
    private final EventGateway eventGateway;

    public AxonBridgeDispatcher(ObjectMapper objectMapper, EventGateway eventGateway) {
        this.objectMapper = objectMapper;
        this.eventGateway = eventGateway;
    }

    public void dispatch(String rawMessageBody) {
        try {
            JsonNode root = objectMapper.readTree(rawMessageBody);
            JsonNode envelopeNode = root.has("Message") && root.has("TopicArn")
                    ? objectMapper.readTree(root.get("Message").asText())
                    : root;

            String messageType = envelopeNode.get("messageType").asText();
            Class<?> type = MESSAGE_TYPES.get(messageType);
            if (type == null) {
                log.debug("[AxonBridge] No saga handler cares about message type {}, ignoring", messageType);
                return;
            }

            Object event = objectMapper.treeToValue(envelopeNode.get("payload"), type);
            eventGateway.publish(event);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to parse orchestrator bridge message", e);
        }
    }
}
