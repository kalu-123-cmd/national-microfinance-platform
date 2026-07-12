package com.microfinance.agent.domain.repository;

import com.microfinance.agent.domain.model.CashTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CashTransactionRepository extends JpaRepository<CashTransaction, String> {
    List<CashTransaction> findByAgentId(String agentId);
    List<CashTransaction> findByUserId(String userId);
}