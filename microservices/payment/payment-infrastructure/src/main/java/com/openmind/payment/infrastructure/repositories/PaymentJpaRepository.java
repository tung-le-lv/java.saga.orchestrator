package com.openmind.payment.infrastructure.repositories;

import com.openmind.payment.domain.aggregates.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

interface PaymentJpaRepository extends JpaRepository<Payment, UUID> {

    Optional<Payment> findFirstByOrderIdOrderByCreatedAtDesc(UUID orderId);
}
