package com.microfinance.kyc.domain.repository;

import com.microfinance.kyc.domain.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface VerificationCheckRepository extends JpaRepository<VerificationCheck, String> {
    List<VerificationCheck> findByApplicationId(String applicationId);
    List<VerificationCheck> findByApplicationIdAndCheckType(String applicationId, CheckType checkType);
    List<VerificationCheck> findByUserId(String userId);
}