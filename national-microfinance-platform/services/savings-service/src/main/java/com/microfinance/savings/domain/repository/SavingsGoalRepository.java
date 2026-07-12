package com.microfinance.savings.domain.repository;

import com.microfinance.savings.domain.model.SavingsGoal;
import com.microfinance.savings.domain.model.GoalStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface SavingsGoalRepository extends JpaRepository<SavingsGoal, String> {

    List<SavingsGoal> findByUserId(String userId);

    List<SavingsGoal> findByUserIdAndStatus(String userId, GoalStatus status);

    List<SavingsGoal> findByAccountId(String accountId);

    /** Goals with auto-save due (last auto-save before cutoff or never) */
    @Query("SELECT g FROM SavingsGoal g WHERE g.status = 'ACTIVE' " +
           "AND g.autoSaveAmount IS NOT NULL " +
           "AND (g.lastAutoSaveAt IS NULL OR g.lastAutoSaveAt <= :cutoff)")
    List<SavingsGoal> findGoalsDueForAutoSave(Instant cutoff);
}
