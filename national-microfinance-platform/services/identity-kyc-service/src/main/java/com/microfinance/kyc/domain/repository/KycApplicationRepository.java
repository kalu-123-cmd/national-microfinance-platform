package com.microfinance.kyc.domain.repository;

import com.microfinance.kyc.domain.model.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface KycApplicationRepository extends JpaRepository<KycApplication, String> {

    List<KycApplication> findByUserId(String userId);

    Optional<KycApplication> findByApplicationNumber(String applicationNumber);

    Optional<KycApplication> findTopByUserIdOrderByCreatedAtDesc(String userId);

    Optional<KycApplication> findTopByUserIdAndStatusOrderByCreatedAtDesc(String userId, KycApplicationStatus status);

    List<KycApplication> findByStatus(KycApplicationStatus status);

    Page<KycApplication> findByStatus(KycApplicationStatus status, Pageable pageable);

    @Query("SELECT k FROM KycApplication k WHERE k.status IN ('SUBMITTED','UNDER_REVIEW','PENDING_REVIEW') ORDER BY k.submittedAt ASC")
    Page<KycApplication> findPendingApplications(Pageable pageable);

    @Modifying
    @Query("UPDATE KycApplication k SET k.status = :status, k.updatedAt = :now WHERE k.id = :id")
    void updateStatus(@Param("id") String id, @Param("status") KycApplicationStatus status, @Param("now") Instant now);

    @Query("SELECT COUNT(k) FROM KycApplication k WHERE k.status = :status")
    long countByStatus(@Param("status") KycApplicationStatus status);

    @Query("SELECT COUNT(k) FROM KycApplication k WHERE k.userId = :userId AND k.status = 'APPROVED'")
    long countApprovedByUserId(@Param("userId") String userId);
}