package com.openmind.order.domain.valueobjects;

import jakarta.persistence.Embeddable;

import java.math.BigDecimal;

/**
 * Represents a monetary amount. Immutable.
 */
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

    public static Money zero() {
        return new Money(BigDecimal.ZERO);
    }

    public Money add(Money other) {
        return new Money(this.amount.add(other.amount));
    }

    @Override
    public String toString() {
        return amount.toString();
    }
}
