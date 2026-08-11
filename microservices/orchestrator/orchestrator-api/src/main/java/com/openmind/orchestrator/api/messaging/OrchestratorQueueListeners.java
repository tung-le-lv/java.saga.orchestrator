package com.openmind.orchestrator.api.messaging;

import io.awspring.cloud.sqs.annotation.SqsListener;
import org.springframework.stereotype.Component;

/**
 * One listener per orchestrator SQS queue, mirroring the original MassTransit receive
 * endpoints of the same names: "orchestrator-order-commands" (just PlaceOrderCommand),
 * "orchestrator-order-events", "orchestrator-payment-events", "orchestrator-fulfillment-events"
 * and "orchestrator-email-events". Every message is handed to the same bridge, which ignores
 * anything it doesn't recognize.
 */
@Component
public class OrchestratorQueueListeners {

    private final AxonBridgeDispatcher dispatcher;

    public OrchestratorQueueListeners(AxonBridgeDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    @SqsListener("orchestrator-order-commands")
    public void onOrderCommands(String body) {
        dispatcher.dispatch(body);
    }

    @SqsListener("orchestrator-order-events")
    public void onOrderEvents(String body) {
        dispatcher.dispatch(body);
    }

    @SqsListener("orchestrator-payment-events")
    public void onPaymentEvents(String body) {
        dispatcher.dispatch(body);
    }

    @SqsListener("orchestrator-fulfillment-events")
    public void onFulfillmentEvents(String body) {
        dispatcher.dispatch(body);
    }

    @SqsListener("orchestrator-email-events")
    public void onEmailEvents(String body) {
        dispatcher.dispatch(body);
    }
}
