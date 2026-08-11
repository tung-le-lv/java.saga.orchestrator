package com.openmind.fulfillment.infrastructure.repositories;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import com.openmind.fulfillment.domain.aggregates.Fulfillment;
import com.openmind.fulfillment.domain.repositories.FulfillmentRepository;
import com.openmind.shared.mongodb.MongoDbContext;
import com.openmind.shared.mongodb.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class FulfillmentRepositoryImpl extends MongoRepository<Fulfillment> implements FulfillmentRepository {

    public FulfillmentRepositoryImpl(MongoDbContext dbContext) {
        super(dbContext, "fulfillments", Fulfillment.class);
    }

    @Override
    public Optional<Fulfillment> findByOrderId(UUID orderId) {
        return Optional.ofNullable(
                collection.find(Filters.eq("orderId", orderId))
                        .sort(Sorts.descending("createdAt"))
                        .first());
    }
}
