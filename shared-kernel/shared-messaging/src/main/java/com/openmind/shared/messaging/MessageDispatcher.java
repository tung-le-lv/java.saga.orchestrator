package com.openmind.shared.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.openmind.shared.integrationmessages.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.GenericTypeResolver;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Routes a raw SQS message body to the {@link IntegrationMessageHandler} registered for its
 * {@code messageType}. A single queue subscribed to a topic (e.g. the Order service's
 * "order-service-commands" queue subscribed to {@code order-commands}) may receive several
 * message types; messages with no registered handler on this queue are ignored.
 */
@Component
@SuppressWarnings({"rawtypes", "unchecked"})
public class MessageDispatcher {

    private static final Logger log = LoggerFactory.getLogger(MessageDispatcher.class);

    private final Map<String, Registration> handlersByMessageType = new HashMap<>();
    private final ObjectMapper objectMapper;

    public MessageDispatcher(List<IntegrationMessageHandler> handlers, ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        for (IntegrationMessageHandler handler : handlers) {
            Class<?> payloadType = GenericTypeResolver.resolveTypeArgument(handler.getClass(), IntegrationMessageHandler.class);
            if (payloadType == null) {
                throw new IllegalStateException("Unable to resolve message type handled by " + handler.getClass());
            }
            handlersByMessageType.put(payloadType.getSimpleName(), new Registration(payloadType, handler));
        }
    }

    public void dispatch(String rawMessageBody) {
        try {
            JsonNode root = objectMapper.readTree(rawMessageBody);
            // Defensive unwrap in case raw message delivery isn't enabled on the SNS subscription.
            JsonNode envelopeNode = root.has("Message") && root.has("TopicArn")
                    ? objectMapper.readTree(root.get("Message").asText())
                    : root;

            String messageType = envelopeNode.get("messageType").asText();
            Registration registration = handlersByMessageType.get(messageType);
            if (registration == null) {
                log.debug("[MessageDispatcher] No handler registered for message type {}, ignoring", messageType);
                return;
            }

            Object message = objectMapper.treeToValue(envelopeNode.get("payload"), registration.payloadType());
            registration.handler().handle((Message) message);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to parse integration message", e);
        }
    }

    private record Registration(Class<?> payloadType, IntegrationMessageHandler handler) {
    }
}
