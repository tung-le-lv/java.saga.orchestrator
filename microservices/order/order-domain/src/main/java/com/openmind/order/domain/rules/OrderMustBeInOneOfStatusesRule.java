package com.openmind.order.domain.rules;

import com.openmind.order.domain.enums.OrderStatus;
import com.openmind.shared.domain.BusinessRule;

import java.util.List;
import java.util.stream.Collectors;

public class OrderMustBeInOneOfStatusesRule implements BusinessRule {

    private final OrderStatus currentStatus;
    private final List<OrderStatus> allowedStatuses;
    private final String action;

    public OrderMustBeInOneOfStatusesRule(OrderStatus currentStatus, List<OrderStatus> allowedStatuses, String action) {
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
        String allowed = allowedStatuses.stream().map(OrderStatus::getDisplayName).collect(Collectors.joining(", "));
        return "Cannot " + action + ": order must be in one of [" + allowed + "] but is in '" + currentStatus.getDisplayName() + "'";
    }
}
