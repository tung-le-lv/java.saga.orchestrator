package com.openmind.fulfillment.application.commands.cancelfulfillment;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CancelFulfillmentCommand(
        @NotNull UUID orderId,
        String reason) {
}
