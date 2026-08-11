package com.openmind.fulfillment.application.queries.getfulfillment;

import com.openmind.shared.application.queries.Query;

import java.util.UUID;

public record GetFulfillmentQuery(UUID orderId) implements Query<FulfillmentDto> {
}
