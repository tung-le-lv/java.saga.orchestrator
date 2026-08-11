package com.openmind.fulfillment.application.commands.fulfillorder;

import java.util.UUID;

public record FulfillmentItemCommand(UUID productId, String productName, int quantity) {
}
