package com.openmind.shared.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.awspring.cloud.sns.core.SnsTemplate;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

/**
 * Publishes an integration command/event to the given SNS topic, wrapped in a
 * {@link MessageEnvelope} so subscriber queues can route by message type.
 */
@Component
public class MessagePublisher {

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
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize message " + messageType, e);
        }
    }
}
