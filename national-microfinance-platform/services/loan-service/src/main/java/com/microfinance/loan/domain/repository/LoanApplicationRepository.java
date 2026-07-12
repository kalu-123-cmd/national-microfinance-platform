package com.microfinance.loan.domain.repository;

import com.microfinance.loan.domain.model.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface LoanApplicationRepository extends JpaRepository<LoanApplication, String> {
    Optional<LoanApplication> findByLoanNumber(String loanNumber);
    List<LoanApplication> findByUserId(String userId);
    Page<LoanApplication> findByStatus(LoanStatus status, Pageable pageable);
    List<LoanApplication> findByUserIdAndStatus(String userId, LoanStatus status);

    @Query("SELECT l FROM LoanApplication l WHERE l.status = 'ACTIVE' AND l.maturityDate < CURRENT_DATE")
    List<LoanApplication> findOverdueLoans();

    @Query("SELECT COUNT(l) FROM LoanApplication l WHERE l.status = :status")
    long countByStatus(@Param("status") LoanStatus status);
}