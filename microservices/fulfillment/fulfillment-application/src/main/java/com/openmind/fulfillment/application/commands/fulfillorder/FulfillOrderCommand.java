package com.openmind.fulfillment.application.commands.fulfillorder;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record FulfillOrderCommand(
        @NotNull UUID orderId,
        @NotNull UUID customerId,
        @NotEmpty List<FulfillmentItemCommand> items,
        String shippingAddress,
        UUID correlationId) {
}
