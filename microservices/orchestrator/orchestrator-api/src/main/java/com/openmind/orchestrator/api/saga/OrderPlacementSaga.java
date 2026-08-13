package com.openmind.orchestrator.api.saga;

import com.openmind.email.contract.commands.SendBackorderEmailCommand;
import com.openmind.email.contract.commands.SendOrderConfirmationEmailCommand;
import com.openmind.email.contract.commands.SendPaymentFailedEmailCommand;
import com.openmind.email.contract.commands.SendRefundEmailCommand;
import com.openmind.email.contract.events.EmailFailedEvent;
import com.openmind.email.contract.events.EmailSentEvent;
import com.openmind.fulfillment.contract.FulfillmentItemDto;
import com.openmind.fulfillment.contract.commands.FulfillOrderCommand;
import com.openmind.fulfillment.contract.events.FulfillmentFailedEvent;
import com.openmind.fulfillment.contract.events.OrderShippedEvent;
import com.openmind.order.contract.OrderItemDto;
import com.openmind.order.contract.commands.MarkOrderAsBackOrderedCommand;
import com.openmind.order.contract.commands.MarkOrderAsPaymentCompletedCommand;
import com.openmind.order.contract.commands.MarkOrderAsPaymentFailedCommand;
import com.openmind.order.contract.commands.MarkOrderAsShippedCommand;
import com.openmind.order.contract.commands.PlaceOrderCommand;
import com.openmind.order.contract.commands.ValidateOrderCommand;
import com.openmind.order.contract.events.OrderValidatedEvent;
import com.openmind.order.contract.events.OrderValidationFailedEvent;
import com.openmind.payment.contract.commands.ProcessPaymentCommand;
import com.openmind.payment.contract.commands.RefundPaymentCommand;
import com.openmind.payment.contract.events.PaymentCompletedEvent;
import com.openmind.payment.contract.events.PaymentFailedEvent;
import com.openmind.payment.contract.events.PaymentRefundedEvent;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.openmind.shared.messaging.MessagePublisher;
import com.openmind.shared.messaging.Topics;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.SagaLifecycle;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.spring.stereotype.Saga;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Order Placement Saga: coordinates the order placement workflow across the Order, Payment,
 * Fulfillment and Email services using Axon's saga model.
 * <p>
 * Axon only manages saga lifecycle, correlation (association by {@code orderId}) and
 * persistence here (Axon's own {@code JpaSagaStore}, autoconfigured). Cross-service dispatch does not go through
 * Axon's CommandBus: each handler below publishes the next integration command directly to
 * SNS via {@link MessagePublisher}, since the target is another microservice reached over
 * SQS, not an in-JVM handler. Inbound integration messages are bridged onto Axon's local
 * event bus by {@link com.openmind.orchestrator.api.messaging.AxonBridgeDispatcher}.
 * <p>
 * Happy path: Validating -&gt; PaymentProcessing -&gt; Fulfilling -&gt; SendingConfirmation -&gt; Completed.
 * Validation failure: Validating -&gt; ValidationFailed (retryable via another PlaceOrderCommand).
 * Payment failure: PaymentProcessing -&gt; PaymentNotPaid (retryable via the Payment API), then continues to Fulfilling.
 * Out of stock: Fulfilling -&gt; RefundingPayment -&gt; SendingBackorderEmail -&gt; SendingRefundEmail -&gt; Cancelled.
 */
// This saga's state lives entirely in private fields with no getters. Axon's default Jackson
// serializer (see application.yml's axon.serializer.general) uses a plain, unconfigured
// ObjectMapper, which only sees public getters/fields by default - so without this, every
// field here would round-trip as null on the next event once the saga is reloaded from the
// JpaSagaStore.
@JsonAutoDetect(
        fieldVisibility = JsonAutoDetect.Visibility.ANY,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE)
@Saga
public class OrderPlacementSaga {

    private static final Logger log = LoggerFactory.getLogger(OrderPlacementSaga.class);

    @Autowired
    private transient MessagePublisher messagePublisher;

    private UUID orderId;
    private UUID correlationId;
    private UUID customerId;
    private BigDecimal totalAmount;
    private String shippingAddress;
    private String customerEmail;
    private String customerName;
    private List<OrderItemDto> items = new ArrayList<>();

    private UUID paymentId;
    private String paymentTransactionId;

    private UUID fulfillmentId;
    private String trackingNumber;
    private Instant estimatedDelivery;

    private String lastError;
    private String lastErrorCode;
    private int retryCount;

    private String currentState;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant completedAt;

    public OrderPlacementSaga() {
    }

    @StartSaga
    @SagaEventHandler(associationProperty = "orderId")
    public void on(PlaceOrderCommand event) {
        if (currentState == null) {
            this.orderId = event.orderId();
            this.correlationId = event.correlationId();
            this.createdAt = Instant.now();
            transitionTo("Validating");

            log.info("[Saga] PlaceOrder - OrderId: {}, CorrelationId: {}", orderId, correlationId);
            publish(Topics.ORDER_COMMANDS, new ValidateOrderCommand(correlationId, orderId));
        } else if ("ValidationFailed".equals(currentState)) {
            log.info("[Saga] Retrying from ValidationFailed - OrderId: {}", orderId);
            this.lastError = null;
            this.retryCount++;
            transitionTo("Validating");

            publish(Topics.ORDER_COMMANDS, new ValidateOrderCommand(correlationId, orderId));
        } else {
            log.warn("[Saga] PlaceOrder ignored (in progress) - OrderId: {}, State: {}", orderId, currentState);
        }
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void on(OrderValidatedEvent event) {
        if (!"Validating".equals(currentState)) {
            return;
        }
        log.info("[Saga] OrderValidated - OrderId: {}", orderId);

        this.customerId = event.customerId();
        this.totalAmount = event.totalAmount();
        this.shippingAddress = event.shippingAddress();
        this.customerEmail = event.customerEmail();
        this.customerName = event.customerName();
        this.items = event.items();
        transitionTo("PaymentProcessing");

        publish(Topics.PAYMENT_COMMANDS, new ProcessPaymentCommand(
                correlationId, orderId, customerId, totalAmount, "CreditCard", "4111111111111111", "12/26"));
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void on(OrderValidationFailedEvent event) {
        if (!"Validating".equals(currentState)) {
            return;
        }
        log.warn("[Saga] OrderValidationFailed - OrderId: {}, Reason: {}", orderId, event.reason());

        this.lastError = event.reason();
        transitionTo("ValidationFailed");
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void on(PaymentCompletedEvent event) {
        if (!("PaymentProcessing".equals(currentState) || "PaymentNotPaid".equals(currentState))) {
            return;
        }
        log.info("[Saga] PaymentCompleted - OrderId: {}, PaymentId: {}", orderId, event.paymentId());

        this.paymentId = event.paymentId();
        this.paymentTransactionId = event.transactionId();
        this.lastError = null;
        this.lastErrorCode = null;
        transitionTo("Fulfilling");

        publish(Topics.ORDER_COMMANDS, new MarkOrderAsPaymentCompletedCommand(correlationId, orderId, event.transactionId()));
        publish(Topics.FULFILLMENT_COMMANDS, new FulfillOrderCommand(
                correlationId, orderId, customerId, toFulfillmentItems(items), shippingAddress));
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void on(PaymentFailedEvent event) {
        if (!"PaymentProcessing".equals(currentState)) {
            return;
        }
        log.warn("[Saga] PaymentFailed - OrderId: {}, Reason: {}", orderId, event.reason());

        this.lastError = event.reason();
        this.lastErrorCode = event.errorCode();
        transitionTo("PaymentNotPaid");

        publish(Topics.ORDER_COMMANDS, new MarkOrderAsPaymentFailedCommand(correlationId, orderId, event.reason()));
        publish(Topics.EMAIL_COMMANDS, new SendPaymentFailedEmailCommand(
                correlationId, orderId, customerId, customerEmail, customerName, event.reason()));
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void on(OrderShippedEvent event) {
        if (!"Fulfilling".equals(currentState)) {
            return;
        }
        log.info("[Saga] OrderShipped - OrderId: {}, TrackingNumber: {}", orderId, event.trackingNumber());

        this.fulfillmentId = event.fulfillmentId();
        this.trackingNumber = event.trackingNumber();
        this.estimatedDelivery = event.estimatedDelivery();
        transitionTo("SendingConfirmation");

        publish(Topics.ORDER_COMMANDS, new MarkOrderAsShippedCommand(correlationId, orderId, event.trackingNumber()));
        publish(Topics.EMAIL_COMMANDS, new SendOrderConfirmationEmailCommand(
                correlationId, orderId, customerId, customerEmail, customerName, totalAmount, trackingNumber));
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void on(FulfillmentFailedEvent event) {
        if (!"Fulfilling".equals(currentState)) {
            return;
        }
        log.warn("[Saga] FulfillmentFailed - OrderId: {}, Reason: {}", orderId, event.reason());

        this.lastError = event.reason();
        transitionTo("RefundingPayment");

        publish(Topics.ORDER_COMMANDS, new MarkOrderAsBackOrderedCommand(correlationId, orderId, event.reason()));
        publish(Topics.PAYMENT_COMMANDS, new RefundPaymentCommand(
                correlationId, orderId, paymentId, totalAmount, "Items out of stock"));
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void on(PaymentRefundedEvent event) {
        if (!"RefundingPayment".equals(currentState)) {
            return;
        }
        log.info("[Saga] PaymentRefunded - OrderId: {}", orderId);

        transitionTo("SendingBackorderEmail");

        publish(Topics.EMAIL_COMMANDS, new SendBackorderEmailCommand(
                correlationId, orderId, customerId, customerEmail, customerName,
                List.of("Items from your order"), Instant.now().plus(14, ChronoUnit.DAYS)));
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void on(EmailSentEvent event) {
        switch (currentState) {
            case "PaymentNotPaid" -> {
                log.info("[Saga] EmailSent (PaymentFailed) - OrderId: {}. Awaiting payment retry.", orderId);
                touch();
            }
            case "SendingBackorderEmail" -> {
                log.info("[Saga] EmailSent (Backorder) - OrderId: {}", orderId);
                transitionTo("SendingRefundEmail");
                publish(Topics.EMAIL_COMMANDS, new SendRefundEmailCommand(
                        correlationId, orderId, customerId, customerEmail, customerName, totalAmount));
            }
            case "SendingRefundEmail" -> {
                log.info("[Saga] EmailSent (Refund) - OrderId: {}. Saga cancelled.", orderId);
                endAs("Cancelled");
            }
            case "SendingConfirmation" -> {
                log.info("[Saga] EmailSent (Confirmation) - OrderId: {}. Saga completed!", orderId);
                endAs("Completed");
            }
            default -> touch();
        }
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void on(EmailFailedEvent event) {
        switch (currentState) {
            case "PaymentNotPaid" -> {
                log.warn("[Saga] EmailFailed (PaymentFailed) - OrderId: {}, Reason: {}. Awaiting payment retry.", orderId, event.reason());
                touch();
            }
            case "SendingBackorderEmail", "SendingRefundEmail" -> {
                log.warn("[Saga] EmailFailed - OrderId: {}, Reason: {}. Saga cancelled.", orderId, event.reason());
                endAs("Cancelled");
            }
            case "SendingConfirmation" -> {
                log.warn("[Saga] EmailFailed (Confirmation) - OrderId: {}, Reason: {}. Order processed.", orderId, event.reason());
                endAs("Completed");
            }
            default -> touch();
        }
    }

    private void transitionTo(String state) {
        this.currentState = state;
        touch();
    }

    private void endAs(String state) {
        this.currentState = state;
        this.completedAt = Instant.now();
        touch();
        SagaLifecycle.end();
    }

    private void touch() {
        this.updatedAt = Instant.now();
    }

    private void publish(String topic, Object message) {
        messagePublisher.publish(topic, message);
    }

    private static List<FulfillmentItemDto> toFulfillmentItems(List<OrderItemDto> items) {
        return items.stream()
                .map(i -> new FulfillmentItemDto(i.productId(), i.productName(), i.quantity()))
                .toList();
    }

    public String getCurrentState() {
        return currentState;
    }
}
