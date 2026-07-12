package com.microfinance.savings.domain.repository;

import com.microfinance.savings.domain.model.SavingsAccount;
import com.microfinance.savings.domain.model.AccountStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface SavingsAccountRepository extends JpaRepository<SavingsAccount, String> {

    List<SavingsAccount> findByUserId(String userId);

    Optional<SavingsAccount> findByAccountNumber(String accountNumber);

    Page<SavingsAccount> findByUserIdAndStatus(String userId, AccountStatus status, Pageable pageable);

    List<SavingsAccount> findByStatus(AccountStatus status);

    boolean existsByAccountNumber(String accountNumber);

    @Query("SELECT SUM(a.balance) FROM SavingsAccount a WHERE a.userId = :userId AND a.status = 'ACTIVE'")
    BigDecimal sumBalanceByUserId(String userId);

    @Query("SELECT COUNT(a) FROM SavingsAccount a WHERE a.userId = :userId AND a.status = 'ACTIVE'")
    Long countActiveByUserId(String userId);
}
