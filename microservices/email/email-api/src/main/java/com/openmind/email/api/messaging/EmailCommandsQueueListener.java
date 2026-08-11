package com.openmind.email.api.messaging;

import com.openmind.shared.messaging.MessageDispatcher;
import io.awspring.cloud.sqs.annotation.SqsListener;
import org.springframework.stereotype.Component;

@Component
public class EmailCommandsQueueListener {

    private final MessageDispatcher dispatcher;

    public EmailCommandsQueueListener(MessageDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    @SqsListener("email-service-commands")
    public void listen(String body) {
        dispatcher.dispatch(body);
    }
}
