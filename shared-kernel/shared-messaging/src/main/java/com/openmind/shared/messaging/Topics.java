package com.openmind.shared.messaging;

/**
 * SNS topic names. Every message belonging to a given group (all order commands, all order
 * events, ...) fans out to the same topic. Each service's SQS queue subscribes to the topics
 * it cares about and routes incoming messages by their {@code messageType} (see
 * {@link MessageEnvelope}).
 */
public final class Topics {

    public static final String ORDER_COMMANDS = "order-commands";
    public static final String ORDER_EVENTS = "order-events";

    public static final String PAYMENT_COMMANDS = "payment-commands";
    public static final String PAYMENT_EVENTS = "payment-events";

    public static final String FULFILLMENT_COMMANDS = "fulfillment-commands";
    public static final String FULFILLMENT_EVENTS = "fulfillment-events";

    public static final String EMAIL_COMMANDS = "email-commands";
    public static final String EMAIL_EVENTS = "email-events";

    private Topics() {
    }
}
