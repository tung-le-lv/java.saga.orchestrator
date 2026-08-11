package com.openmind.payment.domain.valueobjects;

import com.openmind.shared.domain.ValueObject;

import java.math.BigDecimal;
import java.util.List;

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

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
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
