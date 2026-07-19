package com.observe.observe.repositories;

import com.observe.observe.models.Payment;
import com.observe.observe.models.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    Optional<Payment> findByOrder(Order order);
    Optional<Payment> findByTransactionReference(String transactionReference);
}