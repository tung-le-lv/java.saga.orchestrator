package com.openmind.payment.infrastructure.repositories;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import com.openmind.payment.domain.aggregates.Payment;
import com.openmind.payment.domain.repositories.PaymentRepository;
import com.openmind.shared.mongodb.MongoDbContext;
import com.openmind.shared.mongodb.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class PaymentRepositoryImpl extends MongoRepository<Payment> implements PaymentRepository {

    public PaymentRepositoryImpl(MongoDbContext dbContext) {
        super(dbContext, "payments", Payment.class);
    }

    @Override
    public Optional<Payment> findByOrderId(UUID orderId) {
        return Optional.ofNullable(
                collection.find(Filters.eq("orderId", orderId))
                        .sort(Sorts.descending("createdAt"))
                        .first());
    }
}
