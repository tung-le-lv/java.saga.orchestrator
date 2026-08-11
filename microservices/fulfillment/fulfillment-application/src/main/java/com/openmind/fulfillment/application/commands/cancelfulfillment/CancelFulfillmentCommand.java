package com.openmind.fulfillment.application.commands.cancelfulfillment;

import com.openmind.shared.application.commands.Command;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CancelFulfillmentCommand(
        @NotNull UUID orderId,
        String reason) implements Command<Void> {
}
