package com.openmind.order.infrastructure.repositories;

import com.openmind.order.domain.aggregates.Order;
import com.openmind.order.domain.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

interface OrderJpaRepository extends JpaRepository<Order, UUID> {

    List<Order> findByCustomerId_Value(UUID customerId);

    List<Order> findByStatus(OrderStatus status);
}
