# Saga Orchestrator

A comprehensive implementation of the **Saga Orchestrator Pattern** for distributed transactions in a microservices architecture, written in Java (Spring Boot / Maven multi-module), ported from an original .NET implementation.

## Architecture Overview

This solution implements an **Order Placement Orchestrator** that coordinates a distributed transaction across multiple microservices over AWS SNS/SQS (LocalStack for local development):

```
┌─────────────────┐
│   Client/UI     │
└────────┬────────┘
         │
         ├─────────────────────────────────────────────────────┐
         │ 1. POST /api/orders (create order)                  │
         ▼                                                     │
┌─────────────────────────────────────────────────────────────┐│
│                       Order Service                         ││
└─────────────────────────────────────────────────────────────┘│
                                                               │
         ┌─────────────────────────────────────────────────────┘
         │ 2. POST /api/sagas/orders/{orderId}/place
         ▼
┌─────────────────────────────────────────────────────────────┐
│              Order Placement Orchestrator                   │
│                  (Axon Framework Saga)                       │
│                                                             │
│  States: Validating → PaymentProcessing →                   │
│          Fulfilling → SendingConfirmation → Completed       │
│                                                             │
│  Retryable States: ValidationFailed, PaymentNotPaid         │
│                                                             │
│  Error States: RefundingPayment →                            │
│               SendingBackorderEmail → SendingRefundEmail → Cancelled │
└──────────────────────┬──────────────────────────────────────┘
                       │
    ┌──────────────────┼──────────────────┬───────────────────┐
    │                  │                  │                   │
    ▼                  ▼                  ▼                   ▼
┌───────────┐   ┌───────────┐   ┌──────────────┐   ┌──────────┐
│  Order    │   │  Payment  │   │ Fulfillment  │   │  Email   │
│  Service  │   │  Service  │◄──│   Service    │   │ Service  │
│           │   │           │   │              │   │(stateless)│
└─────┬─────┘   └─────┬─────┘   └──────┬───────┘   └──────────┘
      │               │                │                
      ▼               ▼                ▼                
┌───────────┐   ┌───────────┐   ┌──────────────┐   
│  OrderDb  │   │ PaymentDb │   │FulfillmentDb │   
│ (MongoDB) │   │ (MongoDB) │   │  (MongoDB)   │   
└───────────┘   └───────────┘   └──────────────┘

                      ▲
                      │ 3. "retry payment" (when PaymentNotPaid)
                      │    POST /api/payments/order/{orderId}/retry
┌─────────────────────┴───┐
│   Client/UI             │
│   (on payment failure)  │
└─────────────────────────┘
```

All cross-service communication is fan-out messaging over SNS topics (`order-commands`, `order-events`, `payment-commands`, `payment-events`, `fulfillment-commands`, `fulfillment-events`, `email-commands`, `email-events`), each consumed by one or more SQS queues — see `docker/localstack-init/init-topology.sh` for the exact topology.

## Features

### Saga Orchestrator Pattern
- **Centralized Workflow Management**: Single point of control for the entire order placement process
- **State Machine Implementation**: `OrderPlacementSaga` using the Axon Framework's `@Saga`/`@SagaEventHandler` model, correlated by `orderId`
- **Custom MongoDB Saga Store**: Axon ships JPA/JDBC saga stores but no MongoDB one — `MongoSagaStore` fills that gap (collection `order_placement_sagas`)
- **Compensating Transactions**: Automatic rollback capabilities (refunds, cancellations)
- **Queryable State**: `GET /api/sagas/orders/{orderId}` while a saga is active (completed sagas are removed from the store, matching Axon's lifecycle semantics)

### Clean Architecture per Microservice
Order/Payment/Fulfillment each follow Clean Architecture with:
- **Domain Layer**: Entities, Value Objects, Aggregates, Domain Events
- **Application Layer**: hand-rolled CQRS (Commands/Queries) with a `CommandBus`/`QueryBus`, Handlers
- **Infrastructure Layer**: MongoDB repositories
- **API Layer**: Spring MVC REST controllers, SQS listeners, springdoc/Swagger UI

Email is intentionally simpler (no persistence) — it just consumes commands and simulates sending.

### DDD Tactical Patterns
- **Aggregate Roots**: Order, Payment, Fulfillment
- **Value Objects**: Money, Address, CustomerId, OrderId
- **Domain Events**: dispatched via Spring's `ApplicationEventPublisher`, translated to integration events published to SNS
- **Strongly Typed IDs**: Type-safe identifiers
- **Smart Enums**: OrderStatus, PaymentStatus, FulfillmentStatus

### Technology Stack
- **Java 21**, **Maven** multi-module reactor
- **Spring Boot 3.4** (Web, Validation)
- **Axon Framework 4.10** for the saga (lifecycle/correlation/persistence only — cross-service dispatch goes over SNS, not Axon's CommandBus)
- **Spring Cloud AWS** for SNS publish / SQS consume (against LocalStack locally)
- **MongoDB Java driver** (POJO codec, not Spring Data) for persistence
- **Jakarta Bean Validation** for request/command validation
- **springdoc-openapi** for Swagger UI

## Workflow Scenarios

### Happy Path
1. **Create Order** → Order is created in Order Service (stored in MongoDB)
2. **Place Order** → Saga validates order exists via Order Service
3. **Process Payment** → Payment is processed (simulated gateway, ~85% approval)
4. **Fulfill Order** → Items are shipped (simulated warehouse, ~85% in stock)
5. **Send Confirmation** → Email notification sent (simulated, ~95% delivered)
6. **Complete** → Saga finishes successfully

### Payment Failure Path (Retryable)
1. **Process Payment** → Payment declined
2. **Update Order** → Mark as PaymentFailed
3. **Send Notification** → Email customer about payment failure
4. **Await Retry** → Saga enters `PaymentNotPaid` state
5. **Retry Payment** → `POST /api/payments/order/{orderId}/retry` forces the latest payment attempt to succeed
6. **Continue Flow** → Saga continues to Fulfilling → SendingConfirmation → Completed

### Out of Stock Path
1. **Fulfill Order** → Items out of stock
2. **Update Order** → Mark as BackOrdered
3. **Refund Payment** → Compensating transaction
4. **Send Notifications** → Backorder email, then refund email
5. **Cancel** → Saga ends in `Cancelled` state

## Getting Started

### Prerequisites
- JDK 21
- Maven 3.9+
- Docker & Docker Compose

### Build everything
```bash
mvn install -DskipTests
```

### Using Docker Compose (recommended)
```bash
docker compose up --build
```
This starts MongoDB, LocalStack (with the SNS/SQS topology auto-provisioned via `docker/localstack-init`), and all five services.

### Running locally without Docker (for iterating on one service)
```bash
docker compose up -d mongodb localstack
java -jar microservices/order/order-api/target/order-api.jar --server.port=8091
java -jar microservices/payment/payment-api/target/payment-api.jar --server.port=8092
java -jar microservices/fulfillment/fulfillment-api/target/fulfillment-api.jar --server.port=8093
java -jar microservices/email/email-api/target/email-api.jar --server.port=8094
java -jar microservices/orchestrator/orchestrator-api/target/orchestrator-api.jar --server.port=8095
```

## API Endpoints

Ports below are the `docker compose` port mappings (`order`:5001, `payment`:5002, `fulfillment`:5003, `email`:5004, `orchestrator`:5000).

### Step 1: Create Order (Order Service)
```bash
POST http://localhost:5001/api/orders
Content-Type: application/json

{
  "customerId": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
  "items": [
    {
      "productId": "3fa85f64-5717-4562-b3fc-2c963f66afa7",
      "productName": "Laptop",
      "quantity": 1,
      "unitPrice": 999.99
    }
  ],
  "shippingAddress": {
    "street": "123 Main St",
    "city": "New York",
    "state": "NY",
    "zipCode": "10001",
    "country": "USA"
  }
}
```

### Step 2: Place Order / Start Saga (Orchestrator)
```bash
POST http://localhost:5000/api/sagas/orders/{orderId}/place
```

### Get Saga State (while active)
```bash
GET http://localhost:5000/api/sagas/orders/{orderId}
```

### Retry Payment
When payment fails, the saga enters `PaymentNotPaid` state. Use this endpoint to retry:
```bash
POST http://localhost:5002/api/payments/order/{orderId}/retry
```
On success, the saga automatically continues from PaymentNotPaid → Fulfilling → SendingConfirmation/RefundingPayment → Completed/Cancelled.

### Get Order / Payment / Fulfillment status
```bash
GET http://localhost:5001/api/orders/{orderId}
GET http://localhost:5002/api/payments/order/{orderId}
GET http://localhost:5003/api/fulfillments/order/{orderId}
```

### Health Checks
```bash
GET http://localhost:5000/health   # Orchestrator
GET http://localhost:5001/health   # Order
GET http://localhost:5002/health   # Payment
GET http://localhost:5003/health   # Fulfillment
GET http://localhost:5004/health   # Email
```

## Simulating Different Scenarios

The services include built-in simulation for testing (no real payment gateway, warehouse, or email provider):
- **Payment Service**: ~85% approval rate (15% random declines)
- **Fulfillment Service**: ~85% in-stock rate (15% out-of-stock)
- **Email Service**: ~95% delivery rate

Run multiple order requests to see different workflow paths execute.

## Configuration

### MongoDB Connection
Each service has its own database, configured via `mongodb.connection-string` / `mongodb.database-name`
(or the `MONGODB_CONNECTIONSTRING` / `MONGODB_DATABASENAME` env vars in Docker):

| Service | Database |
|---|---|
| Order | `OrderDb` |
| Payment | `PaymentDb` |
| Fulfillment | `FulfillmentDb` |
| Orchestrator | `OrderPlacementOrchestratorDb` |

### AWS / LocalStack
`spring.cloud.aws.sns.endpoint` / `spring.cloud.aws.sqs.endpoint` point at LocalStack (`http://localhost:4566` locally,
`http://localstack:4566` inside Docker). Region and dummy credentials are hardcoded for local development in each
service's `application.yml`.

## Key Concepts

### Saga States
- `Validating` — validating order exists
- `ValidationFailed` — retryable by publishing another PlaceOrderCommand
- `PaymentProcessing` — processing payment
- `PaymentNotPaid` — awaiting a manual payment retry
- `Fulfilling` — shipping order
- `SendingConfirmation` — sending success email
- `RefundingPayment` / `SendingBackorderEmail` / `SendingRefundEmail` — compensation flow
- `Completed` — successfully completed
- `Cancelled` — cancelled (with compensation)

### Integration Messages
Commands (from Orchestrator):
- `ValidateOrderCommand`, `MarkOrderAsPaymentCompletedCommand`, `MarkOrderAsPaymentFailedCommand`, `MarkOrderAsShippedCommand`, `MarkOrderAsBackOrderedCommand`, `CancelOrderCommand`
- `ProcessPaymentCommand`, `RefundPaymentCommand`
- `FulfillOrderCommand`, `CancelFulfillmentCommand`
- `SendOrderConfirmationEmailCommand`, `SendPaymentFailedEmailCommand`, `SendOrderCancelledEmailCommand`, `SendBackorderEmailCommand`, `SendRefundEmailCommand`

Events (to Orchestrator):
- `OrderValidatedEvent` / `OrderValidationFailedEvent`
- `PaymentCompletedEvent` / `PaymentFailedEvent` / `PaymentRefundedEvent`
- `OrderShippedEvent` / `FulfillmentFailedEvent`
- `EmailSentEvent` / `EmailFailedEvent`

## License

MIT License - see [LICENSE](LICENSE) file for details.

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.
