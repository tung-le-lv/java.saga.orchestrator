package com.openmind.shared.domain;

/**
 * Thrown when an {@link BusinessRule} is broken.
 */
public class BusinessRuleValidationException extends DomainException {

    private final BusinessRule brokenRule;

    public BusinessRuleValidationException(BusinessRule brokenRule) {
        super(brokenRule.getMessage());
        this.brokenRule = brokenRule;
    }

    public BusinessRule getBrokenRule() {
        return brokenRule;
    }
}
