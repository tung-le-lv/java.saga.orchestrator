package com.openmind.payment.domain.rules;

import com.openmind.payment.domain.enums.PaymentStatus;
import com.openmind.shared.domain.BusinessRule;

import java.util.List;
import java.util.stream.Collectors;

public class PaymentMustBeInOneOfStatusesRule implements BusinessRule {

    private final PaymentStatus currentStatus;
    private final List<PaymentStatus> allowedStatuses;
    private final String action;

    public PaymentMustBeInOneOfStatusesRule(PaymentStatus currentStatus, List<PaymentStatus> allowedStatuses, String action) {
        this.currentStatus = currentStatus;
        this.allowedStatuses = allowedStatuses;
        this.action = action;
    }

    @Override
    public boolean isBroken() {
        return allowedStatuses.stream().noneMatch(s -> s.equals(currentStatus));
    }

    @Override
    public String getMessage() {
        String allowed = allowedStatuses.stream().map(PaymentStatus::getDisplayName).collect(Collectors.joining(", "));
        return "Cannot " + action + ": payment must be in one of [" + allowed + "] but is in '" + currentStatus.getDisplayName() + "'";
    }
}
