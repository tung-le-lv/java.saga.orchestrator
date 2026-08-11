package com.openmind.shared.application.commands;

/**
 * Marker interface for commands in the CQRS pattern.
 * Commands represent intentions to change state and yield a {@link CommandResult} of {@code TResult}.
 * Use {@code Void} as {@code TResult} for commands that don't return data.
 */
public interface Command<TResult> {
}
