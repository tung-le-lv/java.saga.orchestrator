package com.openmind.payment.api.config;

import org.axonframework.commandhandling.CommandBus;
import org.axonframework.messaging.interceptors.BeanValidationInterceptor;
import org.axonframework.messaging.interceptors.LoggingInterceptor;
import org.springframework.context.annotation.Configuration;

/**
 * Registers Axon's built-in handler interceptors: Jakarta Bean Validation on every command,
 * then before/after logging around the actual handler invocation.
 */
@Configuration
public class AxonConfig {

    public AxonConfig(CommandBus commandBus) {
        commandBus.registerHandlerInterceptor(new BeanValidationInterceptor<>());
        commandBus.registerHandlerInterceptor(new LoggingInterceptor<>());
    }
}
