package com.openmind.shared.domain;

/**
 * Base exception for domain invariant violations.
 */
public class DomainException extends RuntimeException {

    public DomainException(String message) {
        super(message);
    }
}
