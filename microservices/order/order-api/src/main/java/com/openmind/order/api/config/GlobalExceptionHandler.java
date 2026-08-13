package com.openmind.order.api.config;

import com.openmind.shared.domain.BusinessRuleValidationException;
import com.openmind.shared.domain.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.axonframework.commandhandling.CommandExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(CommandExecutionException.class)
    public ResponseEntity<?> handleCommandExecution(CommandExecutionException e) {
        Throwable cause = e.getCause() != null ? e.getCause() : e;

        if (cause instanceof EntityNotFoundException notFound) {
            return handleNotFound(notFound);
        }
        if (cause instanceof BusinessRuleValidationException businessRule) {
            return handleBusinessRule(businessRule);
        }
        if (cause instanceof ConstraintViolationException validation) {
            return handleValidation(validation);
        }
        return handleUnexpected(cause instanceof Exception ex ? ex : e);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<?> handleNotFound(EntityNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
    }

    @ExceptionHandler(BusinessRuleValidationException.class)
    public ResponseEntity<?> handleBusinessRule(BusinessRuleValidationException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<?> handleValidation(ConstraintViolationException e) {
        var errors = e.getConstraintViolations().stream()
                .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                .collect(Collectors.toList());
        return ResponseEntity.badRequest().body(Map.of("errors", errors));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleUnexpected(Exception e) {
        log.error("Unhandled exception", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "An unexpected error occurred"));
    }
}
