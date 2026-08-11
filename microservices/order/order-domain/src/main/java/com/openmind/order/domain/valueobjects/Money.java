package com.openmind.order.domain.valueobjects;

import com.openmind.shared.domain.ValueObject;

import java.math.BigDecimal;
import java.util.List;

/**
 * Represents a monetary amount. Immutable; MongoDB repopulates it via the protected
 * no-arg constructor and setter, same accommodation as the original .NET value objects.
 */
public class Money extends ValueObject {

    private BigDecimal amount;

    // Required for MongoDB deserialization
    protected Money() {
        this.amount = BigDecimal.ZERO;
    }

    private Money(BigDecimal amount) {
        this.amount = amount;
    }

    public static Money create(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Amount cannot be negative");
        }
        return new Money(amount);
    }

    public static Money zero() {
        return new Money(BigDecimal.ZERO);
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Money add(Money other) {
        return new Money(this.amount.add(other.amount));
    }

    @Override
    public String toString() {
        return amount.toString();
    }

    @Override
    protected List<Object> getEqualityComponents() {
        return List.of(amount);
    }
}
