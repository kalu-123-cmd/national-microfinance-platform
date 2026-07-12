package com.microfinance.payment.domain.repository;

import com.microfinance.payment.domain.model.Payment;
import com.microfinance.payment.domain.model.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, String> {
    Optional<Payment> findByReference(String reference);
    Page<Payment> findByUserId(String userId, Pageable pageable);
    List<Payment> findByStatusAndNextRetryAtBefore(PaymentStatus status, Instant now);

    @Query("SELECT p FROM Payment p WHERE p.status = 'PENDING' AND p.nextRetryAt < :now AND p.retryCount < :maxRetries")
    List<Payment> findRetryable(Instant now, int maxRetries);
}
