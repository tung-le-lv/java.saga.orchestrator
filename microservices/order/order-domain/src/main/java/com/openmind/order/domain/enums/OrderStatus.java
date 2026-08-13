package com.openmind.order.domain.enums;

import java.util.Arrays;

public enum OrderStatus {

    PENDING("Pending"),
    PAYMENT_PROCESSING("PaymentProcessing"),
    PAYMENT_COMPLETED("PaymentCompleted"),
    PAYMENT_FAILED("PaymentFailed"),
    FULFILLING("Fulfilling"),
    SHIPPED("Shipped"),
    BACK_ORDERED("BackOrdered"),
    CANCELLED("Cancelled"),
    REFUNDED("Refunded");

    private final String displayName;

    OrderStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static OrderStatus fromDisplayName(String displayName) {
        return Arrays.stream(values())
                .filter(status -> status.displayName.equals(displayName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "'" + displayName + "' is not a valid OrderStatus"));
    }
}
