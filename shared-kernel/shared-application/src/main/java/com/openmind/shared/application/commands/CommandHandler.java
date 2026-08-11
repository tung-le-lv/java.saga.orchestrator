package com.openmind.shared.application.commands;

/**
 * Handles a single {@link Command} type and produces a {@link CommandResult}.
 * Implementations are discovered as Spring beans and wired into the {@link CommandBus}.
 */
public interface CommandHandler<TCommand extends Command<TResult>, TResult> {

    CommandResult<TResult> handle(TCommand command);
}
