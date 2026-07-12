package com.microfinance.savings.domain.repository;

import com.microfinance.savings.domain.model.SavingsTransaction;
import com.microfinance.savings.domain.model.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface SavingsTransactionRepository extends JpaRepository<SavingsTransaction, String> {

    Page<SavingsTransaction> findByAccountId(String accountId, Pageable pageable);

    Page<SavingsTransaction> findByUserId(String userId, Pageable pageable);

    Optional<SavingsTransaction> findByReference(String reference);

    Page<SavingsTransaction> findByAccountIdAndTransactionType(String accountId, TransactionType type, Pageable pageable);

    @Query("SELECT t FROM SavingsTransaction t WHERE t.accountId = :accountId " +
           "AND t.createdAt BETWEEN :from AND :to ORDER BY t.createdAt DESC")
    List<SavingsTransaction> findByAccountIdAndDateRange(String accountId, Instant from, Instant to);
}
