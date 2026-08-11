package com.openmind.fulfillment.domain.repositories;

import com.openmind.fulfillment.domain.aggregates.Fulfillment;
import com.openmind.shared.domain.Repository;

import java.util.Optional;
import java.util.UUID;

public interface FulfillmentRepository extends Repository<Fulfillment> {

    Optional<Fulfillment> findByOrderId(UUID orderId);
}
