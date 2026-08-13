package com.openmind.payment.infrastructure.repositories;

import com.openmind.payment.domain.aggregates.Payment;
import com.openmind.payment.domain.repositories.PaymentRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class PaymentRepositoryImpl implements PaymentRepository {

    private final PaymentJpaRepository jpaRepository;

    public PaymentRepositoryImpl(PaymentJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<Payment> findById(UUID id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<Payment> findAll() {
        return jpaRepository.findAll();
    }

    @Override
    public void add(Payment aggregate) {
        jpaRepository.save(aggregate);
    }

    @Override
    public void update(Payment aggregate) {
        jpaRepository.save(aggregate);
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public Optional<Payment> findByOrderId(UUID orderId) {
        return jpaRepository.findFirstByOrderIdOrderByCreatedAtDesc(orderId);
    }
}
