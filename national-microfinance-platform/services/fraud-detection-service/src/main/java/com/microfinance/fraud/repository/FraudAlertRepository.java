package com.microfinance.fraud.repository;

import com.microfinance.fraud.entity.FraudAlert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FraudAlertRepository extends JpaRepository<FraudAlert, Long> {
    List<FraudAlert> findByUserId(String userId);
    List<FraudAlert> findByTransactionId(String transactionId);
    List<FraudAlert> findByStatus(String status);
}
