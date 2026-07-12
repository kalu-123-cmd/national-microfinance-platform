package com.microfinance.cooperative.domain.repository;

import com.microfinance.cooperative.domain.model.Contribution;
import com.microfinance.cooperative.domain.model.ContributionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ContributionRepository extends JpaRepository<Contribution, String> {
    List<Contribution> findByCooperativeIdAndContributionMonth(String cooperativeId, String month);
    Optional<Contribution> findByCooperativeIdAndMemberIdAndContributionMonth(String coopId, String memberId, String month);
    List<Contribution> findByMemberIdOrderByContributionMonthDesc(String memberId);
    List<Contribution> findByCooperativeIdAndStatus(String cooperativeId, ContributionStatus status);
    @Query("SELECT SUM(c.amount) FROM Contribution c WHERE c.cooperativeId = :coopId AND c.status = 'PAID'")
    BigDecimal sumPaidByCooperative(String coopId);
}
