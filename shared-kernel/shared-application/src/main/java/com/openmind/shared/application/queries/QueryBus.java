package com.openmind.shared.application.queries;

import org.springframework.core.GenericTypeResolver;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Resolves the {@link QueryHandler} registered for a query's runtime type and invokes it.
 * Queries don't mutate state, so unlike {@code CommandBus} no pipeline behaviors are applied.
 */
@Component
@SuppressWarnings({"rawtypes", "unchecked"})
public class QueryBus {

    private final Map<Class<?>, QueryHandler> handlersByQueryType = new HashMap<>();

    public QueryBus(List<QueryHandler> handlers) {
        for (QueryHandler handler : handlers) {
            Class<?>[] typeArguments = GenericTypeResolver.resolveTypeArguments(handler.getClass(), QueryHandler.class);
            if (typeArguments == null) {
                throw new IllegalStateException("Unable to resolve query type handled by " + handler.getClass());
            }
            handlersByQueryType.put(typeArguments[0], handler);
        }
    }

    public <TResult> QueryResult<TResult> send(Query<TResult> query) {
        QueryHandler<Query<TResult>, TResult> handler = handlersByQueryType.get(query.getClass());
        if (handler == null) {
            throw new IllegalStateException("No query handler registered for " + query.getClass());
        }
        return handler.handle(query);
    }
}
