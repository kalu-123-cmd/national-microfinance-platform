package com.microfinance.loan.domain.repository;

import com.microfinance.loan.domain.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface RepaymentScheduleRepository extends JpaRepository<RepaymentSchedule, String> {
    List<RepaymentSchedule> findByLoanIdOrderByInstallmentNumber(String loanId);
    List<RepaymentSchedule> findByLoanIdAndStatus(String loanId, RepaymentStatus status);

    @Query("SELECT r FROM RepaymentSchedule r WHERE r.dueDate <= :date AND r.status = 'PENDING'")
    List<RepaymentSchedule> findDueInstallments(@Param("date") LocalDate date);

    Optional<RepaymentSchedule> findFirstByLoanIdAndStatusOrderByDueDate(String loanId, RepaymentStatus status);
}