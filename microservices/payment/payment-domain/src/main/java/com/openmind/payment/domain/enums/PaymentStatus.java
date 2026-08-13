package com.openmind.payment.domain.enums;

import java.util.Arrays;

public enum PaymentStatus {

    PENDING("Pending"),
    COMPLETED("Completed"),
    FAILED("Failed"),
    REFUNDED("Refunded");

    private final String displayName;

    PaymentStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static PaymentStatus fromDisplayName(String displayName) {
        return Arrays.stream(values())
                .filter(status -> status.displayName.equals(displayName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "'" + displayName + "' is not a valid PaymentStatus"));
    }
}
