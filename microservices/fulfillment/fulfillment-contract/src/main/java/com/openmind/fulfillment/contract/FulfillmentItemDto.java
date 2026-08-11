package com.openmind.fulfillment.contract;

import java.util.UUID;

public record FulfillmentItemDto(
        UUID productId,
        String productName,
        int quantity) {
}
