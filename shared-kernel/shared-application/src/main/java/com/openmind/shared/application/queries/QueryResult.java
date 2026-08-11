package com.openmind.shared.application.queries;

/**
 * Represents the result of a query execution.
 */
public final class QueryResult<TResult> {

    private final boolean success;
    private final TResult data;
    private final String errorMessage;

    private QueryResult(boolean success, TResult data, String errorMessage) {
        this.success = success;
        this.data = data;
        this.errorMessage = errorMessage;
    }

    public static <TResult> QueryResult<TResult> success(TResult data) {
        return new QueryResult<>(true, data, null);
    }

    public static <TResult> QueryResult<TResult> failure(String errorMessage) {
        return new QueryResult<>(false, null, errorMessage);
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
}
