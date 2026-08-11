package com.openmind.fulfillment.application.queries.getfulfillment;

import java.time.Instant;
import java.util.UUID;

public record FulfillmentDto(
        UUID id,
        UUID orderId,
        String status,
        String trackingNumber,
        Instant estimatedDelivery,
        String failureReason) {
}
