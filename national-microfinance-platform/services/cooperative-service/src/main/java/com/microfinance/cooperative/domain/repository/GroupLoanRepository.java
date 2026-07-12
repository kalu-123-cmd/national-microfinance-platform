package com.microfinance.cooperative.domain.repository;

import com.microfinance.cooperative.domain.model.GroupLoan;
import com.microfinance.cooperative.domain.model.GroupLoanStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface GroupLoanRepository extends JpaRepository<GroupLoan, String> {
    List<GroupLoan> findByCooperativeId(String cooperativeId);
    List<GroupLoan> findByApplicantUserId(String userId);
    List<GroupLoan> findByCooperativeIdAndStatus(String cooperativeId, GroupLoanStatus status);
    List<GroupLoan> findByStatus(GroupLoanStatus status);
}
