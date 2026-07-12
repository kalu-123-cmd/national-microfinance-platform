package com.microfinance.cooperative.domain.repository;

import com.microfinance.cooperative.domain.model.RoscaCycle;
import com.microfinance.cooperative.domain.model.RoscaStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface RoscaCycleRepository extends JpaRepository<RoscaCycle, String> {
    List<RoscaCycle> findByCooperativeIdOrderByCycleNumberAsc(String cooperativeId);
    List<RoscaCycle> findByCooperativeIdAndStatus(String cooperativeId, RoscaStatus status);
    List<RoscaCycle> findByStatusAndScheduledDateLessThanEqual(RoscaStatus status, LocalDate date);
    int countByCooperativeId(String cooperativeId);
}
