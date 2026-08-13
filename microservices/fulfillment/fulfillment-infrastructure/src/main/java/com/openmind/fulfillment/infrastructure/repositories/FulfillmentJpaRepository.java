package com.openmind.fulfillment.infrastructure.repositories;

import com.openmind.fulfillment.domain.aggregates.Fulfillment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

interface FulfillmentJpaRepository extends JpaRepository<Fulfillment, UUID> {

    Optional<Fulfillment> findFirstByOrderIdOrderByCreatedAtDesc(UUID orderId);
}
