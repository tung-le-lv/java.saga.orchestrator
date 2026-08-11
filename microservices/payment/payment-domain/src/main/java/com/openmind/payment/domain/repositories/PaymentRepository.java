package com.openmind.payment.domain.repositories;

import com.openmind.payment.domain.aggregates.Payment;
import com.openmind.shared.domain.Repository;

import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository extends Repository<Payment> {

    Optional<Payment> findByOrderId(UUID orderId);
}
