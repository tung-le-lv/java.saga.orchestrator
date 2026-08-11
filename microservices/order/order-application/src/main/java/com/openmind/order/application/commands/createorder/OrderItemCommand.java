package com.openmind.order.application.commands.createorder;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderItemCommand(
        @NotNull(message = "Product ID is required") UUID productId,
        @NotBlank(message = "Product name is required") String productName,
        @Positive(message = "Quantity must be greater than 0") int quantity,
        @Positive(message = "Unit price must be greater than 0") BigDecimal unitPrice) {
}
