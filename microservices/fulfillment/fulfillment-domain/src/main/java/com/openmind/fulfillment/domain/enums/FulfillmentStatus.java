package com.openmind.fulfillment.domain.enums;

import java.util.Arrays;

public enum FulfillmentStatus {

    PENDING("Pending"),
    SHIPPED("Shipped"),
    FAILED("Failed"),
    CANCELLED("Cancelled");

    private final String displayName;

    FulfillmentStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static FulfillmentStatus fromDisplayName(String displayName) {
        return Arrays.stream(values())
                .filter(status -> status.displayName.equals(displayName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "'" + displayName + "' is not a valid FulfillmentStatus"));
    }
}
