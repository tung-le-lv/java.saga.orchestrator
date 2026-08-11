package com.openmind.shared.mongodb;

import com.mongodb.client.MongoDatabase;
import com.openmind.shared.domain.AggregateRoot;
import com.openmind.shared.domain.DomainEvent;
import com.openmind.shared.domain.UnitOfWork;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * MongoDB context that tracks aggregates and dispatches their domain events on save.
 * Similar in spirit to EF Core's DbContext pattern from the original .NET version, but
 * dispatches events through Spring's {@link ApplicationEventPublisher} instead of MediatR.
 */
@Component
public class MongoDbContext implements UnitOfWork {

    private final MongoDatabase database;
    private final ApplicationEventPublisher eventPublisher;
    private final List<AggregateRoot> trackedAggregates = new ArrayList<>();

    public MongoDbContext(MongoDatabase database, ApplicationEventPublisher eventPublisher) {
        this.database = database;
        this.eventPublisher = eventPublisher;
    }

    public MongoDatabase getDatabase() {
        return database;
    }

    /**
     * Tracks an aggregate for domain event dispatching. Uses reference equality first,
     * falling back to id-based equality, replacing any existing tracked instance with the
     * same id.
     */
    public void track(AggregateRoot aggregate) {
        for (int i = 0; i < trackedAggregates.size(); i++) {
            AggregateRoot existing = trackedAggregates.get(i);
            if (existing == aggregate) {
                return;
            }
            if (existing.equals(aggregate)) {
                trackedAggregates.set(i, aggregate);
                return;
            }
        }
        trackedAggregates.add(aggregate);
    }

    /**
     * Dispatches all domain events from tracked aggregates.
     */
    @Override
    public int saveChanges() {
        List<AggregateRoot> aggregatesWithEvents = trackedAggregates.stream()
                .filter(a -> !a.getDomainEvents().isEmpty())
                .toList();

        List<DomainEvent> domainEvents = aggregatesWithEvents.stream()
                .flatMap(a -> a.getDomainEvents().stream())
                .toList();

        aggregatesWithEvents.forEach(AggregateRoot::clearDomainEvents);

        domainEvents.forEach(eventPublisher::publishEvent);

        return trackedAggregates.size();
    }

    public void clearTracking() {
        trackedAggregates.clear();
    }
}
