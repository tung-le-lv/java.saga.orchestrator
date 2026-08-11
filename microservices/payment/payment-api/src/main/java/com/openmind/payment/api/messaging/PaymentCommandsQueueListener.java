package com.openmind.payment.api.messaging;

import com.openmind.shared.messaging.MessageDispatcher;
import io.awspring.cloud.sqs.annotation.SqsListener;
import org.springframework.stereotype.Component;

@Component
public class PaymentCommandsQueueListener {

    private final MessageDispatcher dispatcher;

    public PaymentCommandsQueueListener(MessageDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    @SqsListener("payment-service-commands")
    public void listen(String body) {
        dispatcher.dispatch(body);
    }
}
