package com.microfinance.savings.domain.repository;

import com.microfinance.savings.domain.model.FixedDeposit;
import com.microfinance.savings.domain.model.FixedDepositStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface FixedDepositRepository extends JpaRepository<FixedDeposit, String> {

    List<FixedDeposit> findByUserId(String userId);

    Optional<FixedDeposit> findByDepositNumber(String depositNumber);

    List<FixedDeposit> findByAccountIdAndStatus(String accountId, FixedDepositStatus status);

    /** Find all active FDs that have reached their maturity date (for scheduled job) */
    @Query("SELECT fd FROM FixedDeposit fd WHERE fd.status = 'ACTIVE' AND fd.maturityDate <= :today")
    List<FixedDeposit> findMaturedDeposits(LocalDate today);

    List<FixedDeposit> findByUserIdAndStatus(String userId, FixedDepositStatus status);
}
