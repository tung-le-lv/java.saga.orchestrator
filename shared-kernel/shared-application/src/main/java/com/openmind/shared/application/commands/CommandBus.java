package com.openmind.shared.application.commands;

import org.springframework.core.GenericTypeResolver;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Resolves the {@link CommandHandler} registered for a command's runtime type and invokes it
 * through the registered {@link CommandPipelineBehavior} chain, in the order they're injected
 * (mirrors the MediatR {@code IPipelineBehavior} pipeline this replaces).
 */
@Component
@SuppressWarnings({"rawtypes", "unchecked"})
public class CommandBus {

    private final Map<Class<?>, CommandHandler> handlersByCommandType = new HashMap<>();
    private final List<CommandPipelineBehavior> behaviors;

    public CommandBus(List<CommandHandler> handlers, List<CommandPipelineBehavior> behaviors) {
        this.behaviors = behaviors;
        for (CommandHandler handler : handlers) {
            Class<?>[] typeArguments = GenericTypeResolver.resolveTypeArguments(handler.getClass(), CommandHandler.class);
            if (typeArguments == null) {
                throw new IllegalStateException("Unable to resolve command type handled by " + handler.getClass());
            }
            handlersByCommandType.put(typeArguments[0], handler);
        }
    }

    public <TResult> CommandResult<TResult> send(Command<TResult> command) {
        CommandHandler<Command<TResult>, TResult> handler = handlersByCommandType.get(command.getClass());
        if (handler == null) {
            throw new IllegalStateException("No command handler registered for " + command.getClass());
        }

        CommandHandlerDelegate<TResult> chain = () -> handler.handle(command);
        for (int i = behaviors.size() - 1; i >= 0; i--) {
            CommandPipelineBehavior behavior = behaviors.get(i);
            CommandHandlerDelegate<TResult> next = chain;
            chain = () -> behavior.handle(command, next);
        }
        return chain.next();
    }
}
