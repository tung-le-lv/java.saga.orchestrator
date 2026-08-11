package com.openmind.payment.domain.events;

import com.openmind.shared.domain.DomainEvent;

import java.util.UUID;

public class PaymentFailedDomainEvent extends DomainEvent {

    private final UUID paymentId;
    private final UUID orderId;
    private final String reason;
    private final String errorCode;
    private final UUID correlationId;

    public PaymentFailedDomainEvent(UUID paymentId, UUID orderId, String reason, String errorCode, UUID correlationId) {
        this.paymentId = paymentId;
        this.orderId = orderId;
        this.reason = reason;
        this.errorCode = errorCode;
        this.correlationId = correlationId;
    }

    public UUID getPaymentId() {
        return paymentId;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public String getReason() {
        return reason;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public UUID getCorrelationId() {
        return correlationId;
    }
}
