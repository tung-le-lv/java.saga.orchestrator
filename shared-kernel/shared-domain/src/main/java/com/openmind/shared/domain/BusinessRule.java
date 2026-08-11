package com.openmind.shared.domain;

/**
 * A business rule encapsulates a domain invariant that must be satisfied.
 */
public interface BusinessRule {

    boolean isBroken();

    String getMessage();
}
