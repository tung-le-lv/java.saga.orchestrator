package com.openmind.shared.messaging;

import com.openmind.shared.integrationmessages.Message;

/**
 * Handles a single integration message type consumed off an SQS queue.
 * Implementations are discovered as Spring beans and wired into the {@link MessageDispatcher}
 * by the {@code TMessage} type argument, the same way {@code CommandHandler} beans are wired
 * into the {@code CommandBus}.
 */
public interface IntegrationMessageHandler<TMessage extends Message> {

    void handle(TMessage message);
}
