## Architecture Overview

```mermaid
flowchart TD
    Client(["Client / UI"])
    ClientRetry(["Client / UI (on payment failure)"])

    OrderAPI["Order Service"]
    Saga{{"Orchestrator\nOrder Placement Saga (Axon)"}}
    PaymentAPI["Payment Service"]
    FulfillmentAPI["Fulfillment Service"]
    EmailAPI["Email Service (stateless)"]

    OrderDB[("order_db")]
    PaymentDB[("payment_db")]
    FulfillmentDB[("fulfillment_db")]

    Client -->|"1. POST /api/orders"| OrderAPI
    Client -->|"2. POST /api/orders/{orderId}/place"| OrderAPI
    OrderAPI -->|"PlaceOrderCommand"| Saga

    Saga <-->|"commands / events\n(SNS -> SQS)"| OrderAPI
    Saga <-->|"commands / events\n(SNS -> SQS)"| PaymentAPI
    Saga <-->|"commands / events\n(SNS -> SQS)"| FulfillmentAPI
    Saga <-->|"commands / events\n(SNS -> SQS)"| EmailAPI

    OrderAPI --> OrderDB
    PaymentAPI --> PaymentDB
    FulfillmentAPI --> FulfillmentDB

    ClientRetry -->|"3. POST /api/payments/order/{orderId}/retry"| PaymentAPI
```

The saga's state machine (`Validating → PaymentProcessing → Fulfilling → SendingConfirmation → Completed`,
plus retryable states `ValidationFailed`/`PaymentNotPaid` and the compensation chain
`RefundingPayment → SendingBackorderEmail → SendingRefundEmail → Cancelled`) is implemented in
`OrderPlacementSaga`. The two [Workflow Scenarios](#workflow-scenarios) below trace the exact
commands/events for the success and retry-after-payment-failure cases.

## Workflow Scenarios

Every arrow below is an async message (SNS publish → SQS deliver), not a direct call — see
[Architecture Overview](#architecture-overview). Notes mark the saga's `currentState`.

### Scenario 1: Success (happy path)

```mermaid
sequenceDiagram
    actor Client
    participant Order as Order Service
    participant Saga as Orchestrator (Saga)
    participant Payment as Payment Service
    participant Fulfillment as Fulfillment Service
    participant Email as Email Service

    Client->>Order: POST /api/orders
    Order-->>Client: 201 Created (orderId)
    Client->>Order: POST /api/orders/{orderId}/place
    Order->>Saga: PlaceOrderCommand
    Note over Saga: Validating

    Saga->>Order: ValidateOrderCommand
    Order-->>Saga: OrderValidatedEvent
    Note over Saga: PaymentProcessing

    Saga->>Payment: ProcessPaymentCommand
    Payment-->>Saga: PaymentCompletedEvent
    Note over Saga: Fulfilling

    Saga->>Order: MarkOrderAsPaymentCompletedCommand
    Saga->>Fulfillment: FulfillOrderCommand
    Fulfillment-->>Saga: OrderShippedEvent
    Note over Saga: SendingConfirmation

    Saga->>Order: MarkOrderAsShippedCommand
    Saga->>Email: SendOrderConfirmationEmailCommand
    Email-->>Saga: EmailSentEvent
    Note over Saga: Completed (saga ends, removed from saga store)
```

### Scenario 2: Retry with payment failed

```mermaid
sequenceDiagram
    actor Client
    participant Order as Order Service
    participant Saga as Orchestrator (Saga)
    participant Payment as Payment Service
    participant Fulfillment as Fulfillment Service
    participant Email as Email Service

    Client->>Order: POST /api/orders
    Order-->>Client: 201 Created (orderId)
    Client->>Order: POST /api/orders/{orderId}/place
    Order->>Saga: PlaceOrderCommand
    Note over Saga: Validating

    Saga->>Order: ValidateOrderCommand
    Order-->>Saga: OrderValidatedEvent
    Note over Saga: PaymentProcessing

    Saga->>Payment: ProcessPaymentCommand
    Payment-->>Saga: PaymentFailedEvent
    Note over Saga: PaymentNotPaid

    Saga->>Order: MarkOrderAsPaymentFailedCommand
    Saga->>Email: SendPaymentFailedEmailCommand
    Email-->>Saga: EmailSentEvent
    Note over Saga: still PaymentNotPaid - awaiting manual retry

    rect rgba(255, 130, 130, 0.15)
    Note over Client,Payment: Client-triggered retry (not automatic)
    Client->>Payment: POST /api/payments/order/{orderId}/retry
    Payment-->>Saga: PaymentCompletedEvent
    end
    Note over Saga: Fulfilling

    Saga->>Order: MarkOrderAsPaymentCompletedCommand
    Saga->>Fulfillment: FulfillOrderCommand
    Fulfillment-->>Saga: OrderShippedEvent
    Note over Saga: SendingConfirmation

    Saga->>Order: MarkOrderAsShippedCommand
    Saga->>Email: SendOrderConfirmationEmailCommand
    Email-->>Saga: EmailSentEvent
    Note over Saga: Completed (saga ends, removed from saga store)
```

## Features

### Saga Orchestrator Pattern
- **Centralized Workflow Management**: Single point of control for the entire order placement process
- **State Machine Implementation**: `OrderPlacementSaga` using the Axon Framework's `@Saga`/`@SagaEventHandler` model, correlated by `orderId`
- **Native Axon Saga Persistence**: Axon's own `JpaSagaStore`, autoconfigured against the `orchestrator_db` Postgres database
- **Compensating Transactions**: Automatic rollback capabilities (refunds, cancellations)
- **Queryable State**: `GET /api/sagas/orders/{orderId}` while a saga is active (completed sagas are removed from the store, matching Axon's lifecycle semantics). The orchestrator has no client-facing trigger endpoint - it is only started/retried indirectly, via the Order service publishing a `PlaceOrderCommand`.

### Technology Stack
- **Java 21**, **Maven** multi-module reactor
- **Spring Boot 3.4** (Web, Validation)
- **Axon Framework 4.10** for the saga (lifecycle/correlation/persistence) and, in every service, as the native in-JVM command/query bus (`CommandGateway`/`QueryGateway`) — cross-service dispatch still goes over SNS, not Axon's distributed CommandBus
- **Spring Cloud AWS** for SNS publish / SQS consume (against LocalStack locally)
- **PostgreSQL + Spring Data JPA** for persistence
- **Jakarta Bean Validation** for request/command validation (enforced on commands via Axon's `BeanValidationInterceptor`)

## License

MIT License - see [LICENSE](LICENSE) file for details.

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.
