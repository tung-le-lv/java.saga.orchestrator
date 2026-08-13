package com.openmind.payment.domain.valueobjects;

import jakarta.persistence.Embeddable;

import java.math.BigDecimal;

@Embeddable
public record Money(BigDecimal amount) {

    public Money {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Amount cannot be negative");
        }
    }

    public static Money create(BigDecimal amount) {
        return new Money(amount);
    }

    @Override
    public String toString() {
        return amount.toString();
    }
}
