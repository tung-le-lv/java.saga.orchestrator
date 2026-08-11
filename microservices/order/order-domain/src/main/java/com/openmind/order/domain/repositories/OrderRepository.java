package com.openmind.order.domain.repositories;

import com.openmind.order.domain.aggregates.Order;
import com.openmind.shared.domain.Repository;

import java.util.List;
import java.util.UUID;

public interface OrderRepository extends Repository<Order> {

    List<Order> findByCustomerId(UUID customerId);

    List<Order> findByStatus(String status);
}
