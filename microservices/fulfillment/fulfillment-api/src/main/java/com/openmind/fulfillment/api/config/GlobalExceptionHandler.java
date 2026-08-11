package com.openmind.fulfillment.api.config;

import com.openmind.shared.application.commands.CommandValidationException;
import com.openmind.shared.domain.BusinessRuleValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(CommandValidationException.class)
    public ResponseEntity<?> handleValidation(CommandValidationException e) {
        return ResponseEntity.badRequest().body(Map.of("errors", e.getErrors()));
    }

    @ExceptionHandler(BusinessRuleValidationException.class)
    public ResponseEntity<?> handleBusinessRule(BusinessRuleValidationException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleUnexpected(Exception e) {
        log.error("Unhandled exception", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "An unexpected error occurred"));
    }
}
