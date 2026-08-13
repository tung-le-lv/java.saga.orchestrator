package com.openmind.order.api.messaging;

import com.openmind.shared.messaging.MessageDispatcher;
import io.awspring.cloud.sqs.annotation.SqsListener;
import org.springframework.stereotype.Component;

/**
 * Listens to the "order-service-commands" queue (subscribed to the {@code order-commands}
 * SNS topic) and routes each message to its registered {@code IntegrationMessageHandler} by
 * envelope message type.
 */
@Component
public class OrderCommandsQueueListener {

    private final MessageDispatcher dispatcher;

    public OrderCommandsQueueListener(MessageDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    @SqsListener("order-service-commands")
    public void listen(String body) {
        dispatcher.dispatch(body);
    }
}
