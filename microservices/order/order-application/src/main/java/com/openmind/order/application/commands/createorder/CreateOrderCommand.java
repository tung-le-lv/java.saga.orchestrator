package com.openmind.order.application.commands.createorder;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record CreateOrderCommand(
        @NotNull(message = "Order ID is required") UUID orderId,
        @NotNull(message = "Customer ID is required") UUID customerId,
        @NotEmpty(message = "At least one item is required") @Valid List<OrderItemCommand> items,
        @NotBlank(message = "Street is required") String street,
        @NotBlank(message = "City is required") String city,
        String state,
        String zipCode,
        @NotBlank(message = "Country is required") String country) {
}
