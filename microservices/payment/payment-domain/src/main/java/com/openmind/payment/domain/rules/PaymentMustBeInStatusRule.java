package com.openmind.payment.domain.rules;

import com.openmind.payment.domain.enums.PaymentStatus;
import com.openmind.shared.domain.BusinessRule;

public class PaymentMustBeInStatusRule implements BusinessRule {

    private final PaymentStatus currentStatus;
    private final PaymentStatus requiredStatus;
    private final String action;

    public PaymentMustBeInStatusRule(PaymentStatus currentStatus, PaymentStatus requiredStatus, String action) {
        this.currentStatus = currentStatus;
        this.requiredStatus = requiredStatus;
        this.action = action;
    }

    @Override
    public boolean isBroken() {
        return !currentStatus.equals(requiredStatus);
    }

    @Override
    public String getMessage() {
        return "Cannot " + action + ": payment must be in status '" + requiredStatus.getName()
                + "' but is in '" + currentStatus.getName() + "'";
    }
}
