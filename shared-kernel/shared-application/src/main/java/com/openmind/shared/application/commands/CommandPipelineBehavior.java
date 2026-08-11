package com.openmind.shared.application.commands;

/**
 * Cross-cutting behavior that wraps command execution (validation, logging, domain event
 * dispatch, ...). Behaviors registered as Spring beans are applied by the {@link CommandBus}
 * in the order Spring injects the {@code List<CommandPipelineBehavior>} (declaration/bean order).
 */
public interface CommandPipelineBehavior {

    <TResult> CommandResult<TResult> handle(Command<TResult> command, CommandHandlerDelegate<TResult> next);
}
