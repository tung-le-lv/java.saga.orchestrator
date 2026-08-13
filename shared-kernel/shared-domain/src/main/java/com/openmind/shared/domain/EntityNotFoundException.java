package com.openmind.shared.domain;

/**
 * Thrown by a command handler when the aggregate targeted by the command doesn't exist.
 */
public class EntityNotFoundException extends DomainException {

    public EntityNotFoundException(String message) {
        super(message);
    }
}
