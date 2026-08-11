package com.openmind.order.infrastructure.repositories;

import com.mongodb.client.model.Filters;
import com.openmind.order.domain.aggregates.Order;
import com.openmind.order.domain.enums.OrderStatus;
import com.openmind.order.domain.repositories.OrderRepository;
import com.openmind.shared.mongodb.MongoDbContext;
import com.openmind.shared.mongodb.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class OrderRepositoryImpl extends MongoRepository<Order> implements OrderRepository {

    public OrderRepositoryImpl(MongoDbContext dbContext) {
        super(dbContext, "orders", Order.class);
    }

    @Override
    public List<Order> findByCustomerId(UUID customerId) {
        return collection.find(Filters.eq("customerId.value", customerId)).into(new java.util.ArrayList<>());
    }

    @Override
    public List<Order> findByStatus(String status) {
        OrderStatus orderStatus = OrderStatus.fromDisplayName(status);
        return collection.find(Filters.eq("status", orderStatus)).into(new java.util.ArrayList<>());
    }
}
