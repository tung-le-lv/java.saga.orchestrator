package com.openmind.shared.application.commands;

/**
 * Represents the result of a command execution.
 */
public final class CommandResult<TResult> {

    private final boolean success;
    private final TResult data;
    private final String errorMessage;
    private final String errorCode;

    private CommandResult(boolean success, TResult data, String errorMessage, String errorCode) {
        this.success = success;
        this.data = data;
        this.errorMessage = errorMessage;
        this.errorCode = errorCode;
    }

    public static <TResult> CommandResult<TResult> success(TResult data) {
        return new CommandResult<>(true, data, null, null);
    }

    public static <TResult> CommandResult<TResult> failure(String errorMessage) {
        return failure(errorMessage, null);
    }

    public static <TResult> CommandResult<TResult> failure(String errorMessage, String errorCode) {
        return new CommandResult<>(false, null, errorMessage, errorCode);
    }

    public boolean isSuccess() {
        return success;
    }

    public TResult getData() {
        return data;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
