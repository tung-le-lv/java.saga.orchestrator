package com.openmind.order.domain.rules;

import com.openmind.order.domain.enums.OrderStatus;
import com.openmind.shared.domain.BusinessRule;

public class OrderMustBeInStatusRule implements BusinessRule {

    private final OrderStatus currentStatus;
    private final OrderStatus requiredStatus;
    private final String action;

    public OrderMustBeInStatusRule(OrderStatus currentStatus, OrderStatus requiredStatus, String action) {
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
        return "Cannot " + action + ": order must be in status '" + requiredStatus.getName()
                + "' but is in '" + currentStatus.getName() + "'";
    }
}
