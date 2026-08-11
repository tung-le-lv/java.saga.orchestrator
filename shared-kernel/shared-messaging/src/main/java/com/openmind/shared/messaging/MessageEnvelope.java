package com.openmind.shared.messaging;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Wire format published to SNS and read back off SQS. A queue subscribed to a topic may see
 * several message types fan through it (e.g. the Order service's commands queue receives
 * {@code ValidateOrderCommand}, {@code MarkOrderAsShippedCommand}, ...), so every message
 * carries its simple class name as a discriminator alongside the JSON payload.
 */
public record MessageEnvelope(String messageType, JsonNode payload) {
}
