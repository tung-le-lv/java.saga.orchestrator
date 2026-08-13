package com.openmind.fulfillment.infrastructure.repositories;

import com.openmind.fulfillment.domain.aggregates.Fulfillment;
import com.openmind.fulfillment.domain.repositories.FulfillmentRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class FulfillmentRepositoryImpl implements FulfillmentRepository {

    private final FulfillmentJpaRepository jpaRepository;

    public FulfillmentRepositoryImpl(FulfillmentJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<Fulfillment> findById(UUID id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<Fulfillment> findAll() {
        return jpaRepository.findAll();
    }

    @Override
    public void add(Fulfillment aggregate) {
        jpaRepository.save(aggregate);
    }

    @Override
    public void update(Fulfillment aggregate) {
        jpaRepository.save(aggregate);
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public Optional<Fulfillment> findByOrderId(UUID orderId) {
        return jpaRepository.findFirstByOrderIdOrderByCreatedAtDesc(orderId);
    }
}
