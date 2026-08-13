package com.openmind.order.infrastructure.repositories;

import com.openmind.order.domain.aggregates.Order;
import com.openmind.order.domain.enums.OrderStatus;
import com.openmind.order.domain.repositories.OrderRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class OrderRepositoryImpl implements OrderRepository {

    private final OrderJpaRepository jpaRepository;

    public OrderRepositoryImpl(OrderJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<Order> findById(UUID id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<Order> findAll() {
        return jpaRepository.findAll();
    }

    @Override
    public void add(Order aggregate) {
        jpaRepository.save(aggregate);
    }

    @Override
    public void update(Order aggregate) {
        jpaRepository.save(aggregate);
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public List<Order> findByCustomerId(UUID customerId) {
        return jpaRepository.findByCustomerId_Value(customerId);
    }

    @Override
    public List<Order> findByStatus(String status) {
        return jpaRepository.findByStatus(OrderStatus.fromDisplayName(status));
    }
}
