package com.openmind.fulfillment.domain.enums;

import com.openmind.shared.domain.Enumeration;

public final class FulfillmentStatus extends Enumeration {

    public static final FulfillmentStatus PENDING = new FulfillmentStatus(1, "Pending");
    public static final FulfillmentStatus SHIPPED = new FulfillmentStatus(2, "Shipped");
    public static final FulfillmentStatus FAILED = new FulfillmentStatus(3, "Failed");
    public static final FulfillmentStatus CANCELLED = new FulfillmentStatus(4, "Cancelled");

    // Required for MongoDB deserialization
    protected FulfillmentStatus() {
        super();
    }

    public FulfillmentStatus(int id, String name) {
        super(id, name);
    }

    public static FulfillmentStatus fromDisplayName(String displayName) {
        return Enumeration.fromDisplayName(FulfillmentStatus.class, displayName);
    }
}
