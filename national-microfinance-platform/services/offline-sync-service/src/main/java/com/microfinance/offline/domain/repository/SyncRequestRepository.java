package com.microfinance.offline.domain.repository;

import com.microfinance.offline.domain.model.SyncRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SyncRequestRepository extends JpaRepository<SyncRequest, String> {
    List<SyncRequest> findByUserIdAndStatus(String userId, String status);
    List<SyncRequest> findByDeviceIdAndStatus(String deviceId, String status);
    List<SyncRequest> findByEntityIdAndEntityTypeOrderByClientTimestampDesc(String entityId, String entityType);
}
