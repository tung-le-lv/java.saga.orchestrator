package com.openmind.shared.integrationmessages;

import java.util.UUID;

/**
 * Base contract for messages exchanged between services over SNS/SQS.
 * The correlation id ties every message belonging to the same saga instance together.
 */
public interface Message {

    UUID getCorrelationId();
}
