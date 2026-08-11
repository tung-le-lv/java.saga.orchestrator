package com.openmind.shared.application.queries;

/**
 * Handles a single {@link Query} type and produces a {@link QueryResult}.
 */
public interface QueryHandler<TQuery extends Query<TResult>, TResult> {

    QueryResult<TResult> handle(TQuery query);
}
