package com.openmind.shared.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.awspring.cloud.sns.core.SnsTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

/**
 * Publishes an integration command/event to the given SNS topic, wrapped in a
 * {@link MessageEnvelope} so subscriber queues can route by message type.
 */
@Component
public class MessagePublisher {

    private static final Logger log = LoggerFactory.getLogger(MessagePublisher.class);

    private final SnsTemplate snsTemplate;
    private final ObjectMapper objectMapper;

    public MessagePublisher(SnsTemplate snsTemplate, ObjectMapper objectMapper) {
        this.snsTemplate = snsTemplate;
        this.objectMapper = objectMapper;
    }

    public void publish(String topicName, Object message) {
        String messageType = message.getClass().getSimpleName();
        try {
            MessageEnvelope envelope = new MessageEnvelope(messageType, objectMapper.valueToTree(message));
            String body = objectMapper.writeValueAsString(envelope);

            snsTemplate.send(topicName, MessageBuilder.withPayload(body).build());
            log.info("[MessagePublisher] Published {} to topic {}", messageType, topicName);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize message " + messageType, e);
        }
    }
}
