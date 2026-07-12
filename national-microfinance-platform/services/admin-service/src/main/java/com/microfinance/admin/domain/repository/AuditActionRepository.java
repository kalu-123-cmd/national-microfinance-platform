package com.microfinance.admin.domain.repository;

import com.microfinance.admin.domain.model.AuditAction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditActionRepository extends JpaRepository<AuditAction, String> {
    Page<AuditAction> findByAdminUserId(String adminUserId, Pageable pageable);
    Page<AuditAction> findByAction(String action, Pageable pageable);
    Page<AuditAction> findByTargetEntityTypeAndTargetEntityId(String type, String id, Pageable pageable);
}
