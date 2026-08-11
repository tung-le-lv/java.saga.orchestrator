package com.openmind.shared.mongodb;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.openmind.shared.domain.AggregateRoot;
import com.openmind.shared.domain.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Generic MongoDB repository implementation.
 * Automatically tracks aggregates in {@link MongoDbContext} for domain event dispatching.
 */
public abstract class MongoRepository<TAggregate extends AggregateRoot>
        implements Repository<TAggregate> {

    protected final MongoCollection<TAggregate> collection;
    private final MongoDbContext dbContext;

    protected MongoRepository(MongoDatabase database, String collectionName, Class<TAggregate> aggregateType) {
        this.collection = database.getCollection(collectionName, aggregateType);
        this.dbContext = null;
    }

    protected MongoRepository(MongoDbContext dbContext, String collectionName, Class<TAggregate> aggregateType) {
        this.dbContext = dbContext;
        this.collection = dbContext.getDatabase().getCollection(collectionName, aggregateType);
    }

    @Override
    public Optional<TAggregate> findById(UUID id) {
        return Optional.ofNullable(collection.find(Filters.eq("_id", id)).first());
    }

    @Override
    public List<TAggregate> findAll() {
        return collection.find().into(new ArrayList<>());
    }

    @Override
    public void add(TAggregate aggregate) {
        collection.insertOne(aggregate);
        trackIfPresent(aggregate);
    }

    @Override
    public void update(TAggregate aggregate) {
        aggregate.incrementVersion();
        collection.replaceOne(Filters.eq("_id", aggregate.getId()), aggregate);
        trackIfPresent(aggregate);
    }

    @Override
    public void deleteById(UUID id) {
        collection.deleteOne(Filters.eq("_id", id));
    }

    private void trackIfPresent(TAggregate aggregate) {
        if (dbContext != null) {
            dbContext.track(aggregate);
        }
    }
}
