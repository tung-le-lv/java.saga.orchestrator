#!/bin/bash
# Provisions the SNS topics / SQS queues / subscriptions used by the fan-out messaging
# topology (see shared-kernel/shared-messaging/.../Topics.java). Runs automatically on
# LocalStack startup via /etc/localstack/init/ready.d/.
set -euo pipefail

REGION="us-east-1"

create_topic() {
  awslocal sns create-topic --name "$1" --region "$REGION" >/dev/null
  echo "Created topic: $1"
}

create_queue() {
  awslocal sqs create-queue --queue-name "$1" --region "$REGION" >/dev/null
  echo "Created queue: $1"
}

subscribe() {
  local topic_name=$1
  local queue_name=$2
  local topic_arn="arn:aws:sns:${REGION}:000000000000:${topic_name}"
  local queue_arn="arn:aws:sqs:${REGION}:000000000000:${queue_name}"

  awslocal sns subscribe \
    --topic-arn "$topic_arn" \
    --protocol sqs \
    --notification-endpoint "$queue_arn" \
    --attributes '{"RawMessageDelivery":"true"}' \
    --region "$REGION" >/dev/null
  echo "Subscribed $queue_name to $topic_name (raw delivery)"
}

# Fan-out topics: every message belonging to a group publishes to the same topic.
create_topic order-commands
create_topic order-events
create_topic payment-commands
create_topic payment-events
create_topic fulfillment-commands
create_topic fulfillment-events
create_topic email-commands
create_topic email-events

# Order service: receives its own commands (ValidateOrder, MarkOrderAs*, CancelOrder, ...).
create_queue order-service-commands
subscribe order-commands order-service-commands

# Payment service: receives ProcessPaymentCommand / RefundPaymentCommand.
create_queue payment-service-commands
subscribe payment-commands payment-service-commands

# Fulfillment service: receives FulfillOrderCommand / CancelFulfillmentCommand.
create_queue fulfillment-service-commands
subscribe fulfillment-commands fulfillment-service-commands

# Email service: receives all Send*EmailCommand messages.
create_queue email-service-commands
subscribe email-commands email-service-commands

# Orchestrator: starts sagas from PlaceOrderCommand, and reacts to every service's events.
create_queue orchestrator-order-commands
subscribe order-commands orchestrator-order-commands

create_queue orchestrator-order-events
subscribe order-events orchestrator-order-events

create_queue orchestrator-payment-events
subscribe payment-events orchestrator-payment-events

create_queue orchestrator-fulfillment-events
subscribe fulfillment-events orchestrator-fulfillment-events

create_queue orchestrator-email-events
subscribe email-events orchestrator-email-events

echo "LocalStack SNS/SQS topology provisioned."
