package com.openmind.payment.domain.enums;

import com.openmind.shared.domain.Enumeration;

public final class PaymentStatus extends Enumeration {

    public static final PaymentStatus PENDING = new PaymentStatus(1, "Pending");
    public static final PaymentStatus COMPLETED = new PaymentStatus(2, "Completed");
    public static final PaymentStatus FAILED = new PaymentStatus(3, "Failed");
    public static final PaymentStatus REFUNDED = new PaymentStatus(4, "Refunded");

    // Required for MongoDB deserialization
    protected PaymentStatus() {
        super();
    }

    public PaymentStatus(int id, String name) {
        super(id, name);
    }

    public static PaymentStatus fromDisplayName(String displayName) {
        return Enumeration.fromDisplayName(PaymentStatus.class, displayName);
    }
}
