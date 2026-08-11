package com.openmind.shared.application.queries;

/**
 * Marker interface for queries in the CQRS pattern. Queries read state and yield a
 * {@link QueryResult} of {@code TResult}.
 */
public interface Query<TResult> {
}
