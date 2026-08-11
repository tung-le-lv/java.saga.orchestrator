package com.openmind.shared.application.commands;

/**
 * Continuation passed to a {@link CommandPipelineBehavior}, invoking the next behavior
 * (or the terminal command handler) in the pipeline.
 */
@FunctionalInterface
public interface CommandHandlerDelegate<TResult> {

    CommandResult<TResult> next();
}
