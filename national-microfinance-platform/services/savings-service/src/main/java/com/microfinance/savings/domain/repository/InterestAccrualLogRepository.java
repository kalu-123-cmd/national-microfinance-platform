package com.microfinance.savings.domain.repository;

import com.microfinance.savings.domain.model.InterestAccrualLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface InterestAccrualLogRepository extends JpaRepository<InterestAccrualLog, String> {

    Optional<InterestAccrualLog> findByAccountIdAndAccrualDate(String accountId, LocalDate date);

    /** All unposted accruals (for end-of-month posting job) */
    @Query("SELECT l FROM InterestAccrualLog l WHERE l.posted = FALSE ORDER BY l.accrualDate ASC")
    List<InterestAccrualLog> findUnpostedAccruals();

    List<InterestAccrualLog> findByAccountIdOrderByAccrualDateDesc(String accountId);
}
