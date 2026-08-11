package com.openmind.shared.application.commands;

import java.util.List;

/**
 * Thrown by {@code ValidationBehavior} when a command fails Bean Validation.
 */
public class CommandValidationException extends RuntimeException {

    private final List<String> errors;

    public CommandValidationException(List<String> errors) {
        super(String.join("; ", errors));
        this.errors = errors;
    }

    public List<String> getErrors() {
        return errors;
    }
}
