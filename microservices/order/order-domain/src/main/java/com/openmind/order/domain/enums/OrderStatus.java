package com.openmind.order.domain.enums;

import com.openmind.shared.domain.Enumeration;

/**
 * Smart enum for order status, following the same "smart enum" pattern as the original
 * .NET {@code OrderStatus : Enumeration}.
 */
public final class OrderStatus extends Enumeration {

    public static final OrderStatus PENDING = new OrderStatus(1, "Pending");
    public static final OrderStatus PAYMENT_PROCESSING = new OrderStatus(2, "PaymentProcessing");
    public static final OrderStatus PAYMENT_COMPLETED = new OrderStatus(3, "PaymentCompleted");
    public static final OrderStatus PAYMENT_FAILED = new OrderStatus(4, "PaymentFailed");
    public static final OrderStatus FULFILLING = new OrderStatus(5, "Fulfilling");
    public static final OrderStatus SHIPPED = new OrderStatus(6, "Shipped");
    public static final OrderStatus BACK_ORDERED = new OrderStatus(7, "BackOrdered");
    public static final OrderStatus CANCELLED = new OrderStatus(8, "Cancelled");
    public static final OrderStatus REFUNDED = new OrderStatus(9, "Refunded");

    // Required for MongoDB deserialization
    protected OrderStatus() {
        super();
    }

    public OrderStatus(int id, String name) {
        super(id, name);
    }

    public static OrderStatus fromDisplayName(String displayName) {
        return Enumeration.fromDisplayName(OrderStatus.class, displayName);
    }

    public static OrderStatus fromValue(int value) {
        return Enumeration.fromValue(OrderStatus.class, value);
    }
}
