package com.openmind.fulfillment.domain.rules;

import com.openmind.fulfillment.domain.enums.FulfillmentStatus;
import com.openmind.shared.domain.BusinessRule;

public class FulfillmentMustBeInStatusRule implements BusinessRule {

    private final FulfillmentStatus currentStatus;
    private final FulfillmentStatus requiredStatus;
    private final String action;

    public FulfillmentMustBeInStatusRule(FulfillmentStatus currentStatus, FulfillmentStatus requiredStatus, String action) {
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
        return "Cannot " + action + ": fulfillment must be in status '" + requiredStatus.getName()
                + "' but is in '" + currentStatus.getName() + "'";
    }
}
