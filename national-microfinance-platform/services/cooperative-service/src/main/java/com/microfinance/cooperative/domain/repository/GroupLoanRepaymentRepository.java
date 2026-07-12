package com.microfinance.cooperative.domain.repository;

import com.microfinance.cooperative.domain.model.GroupLoanRepayment;
import com.microfinance.cooperative.domain.model.RepaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface GroupLoanRepaymentRepository extends JpaRepository<GroupLoanRepayment, String> {
    List<GroupLoanRepayment> findByLoanIdOrderByInstallmentNumberAsc(String loanId);
    List<GroupLoanRepayment> findByLoanIdAndStatus(String loanId, RepaymentStatus status);
    List<GroupLoanRepayment> findByStatusAndDueDateLessThan(RepaymentStatus status, LocalDate date);
}
