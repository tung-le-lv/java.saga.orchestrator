package com.openmind.shared.application.behaviors;

import com.openmind.shared.application.commands.Command;
import com.openmind.shared.application.commands.CommandHandlerDelegate;
import com.openmind.shared.application.commands.CommandPipelineBehavior;
import com.openmind.shared.application.commands.CommandResult;
import com.openmind.shared.application.commands.CommandValidationException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

/**
 * Pipeline behavior that validates a command using Jakarta Bean Validation before it reaches
 * its handler.
 */
@Component
@Order(0)
public class ValidationBehavior implements CommandPipelineBehavior {

    private final Validator validator;

    public ValidationBehavior(Validator validator) {
        this.validator = validator;
    }

    @Override
    public <TResult> CommandResult<TResult> handle(Command<TResult> command, CommandHandlerDelegate<TResult> next) {
        Set<ConstraintViolation<Command<TResult>>> violations = validator.validate(command);
        if (!violations.isEmpty()) {
            List<String> errors = violations.stream()
                    .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                    .toList();
            throw new CommandValidationException(errors);
        }
        return next.next();
    }
}
