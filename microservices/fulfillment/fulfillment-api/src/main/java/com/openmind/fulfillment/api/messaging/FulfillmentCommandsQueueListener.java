package com.openmind.fulfillment.api.messaging;

import com.openmind.shared.messaging.MessageDispatcher;
import io.awspring.cloud.sqs.annotation.SqsListener;
import org.springframework.stereotype.Component;

@Component
public class FulfillmentCommandsQueueListener {

    private final MessageDispatcher dispatcher;

    public FulfillmentCommandsQueueListener(MessageDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    @SqsListener("fulfillment-service-commands")
    public void listen(String body) {
        dispatcher.dispatch(body);
    }
}
