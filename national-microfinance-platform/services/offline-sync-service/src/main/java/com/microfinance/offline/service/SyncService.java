package com.microfinance.offline.service;

import com.microfinance.offline.domain.model.SyncRequest;
import com.microfinance.offline.domain.repository.SyncRequestRepository;
import com.microfinance.offline.dto.SyncBatchRequest;
import com.microfinance.offline.dto.SyncResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SyncService {

    private final SyncRequestRepository syncRequestRepository;

    @Transactional
    public SyncResponse processBatch(SyncBatchRequest batch) {
        int processed = 0;
        List<String> failedIds = new ArrayList<>();

        for (SyncRequest item : batch.getItems()) {
            try {
                // Assign server-side ID if missing
                if (item.getId() == null || item.getId().isBlank()) {
                    item.setId(UUID.randomUUID().toString());
                }
                item.setDeviceId(batch.getDeviceId());
                item.setUserId(batch.getUserId());
                item.setStatus("PENDING");
                item.setRetryCount(0);

                // Check for conflict: latest server-side version of same entity
                List<SyncRequest> existing = syncRequestRepository
                        .findByEntityIdAndEntityTypeOrderByClientTimestampDesc(
                                item.getEntityId(), item.getEntityType());

                if (!existing.isEmpty()) {
                    SyncRequest latest = existing.get(0);
                    // If server has newer data, mark as CONFLICT
                    if (latest.getServerTimestamp() != null &&
                            item.getClientTimestamp() < latest.getServerTimestamp().toEpochMilli()) {
                        item.setStatus("CONFLICT");
                        item.setErrorMessage("Conflict: server has newer version");
                    }
                }

                syncRequestRepository.save(item);

                // Process the sync operation
                if (!"CONFLICT".equals(item.getStatus())) {
                    processSyncItem(item);
                    processed++;
                }
            } catch (Exception e) {
                log.error("Failed to process sync item {}: {}", item.getId(), e.getMessage());
                failedIds.add(item.getId());
                item.setStatus("FAILED");
                item.setErrorMessage(e.getMessage());
                item.setRetryCount(item.getRetryCount() != null ? item.getRetryCount() + 1 : 1);
                syncRequestRepository.save(item);
            }
        }

        return SyncResponse.builder()
                .received(batch.getItems().size())
                .processed(processed)
                .failed(failedIds.size())
                .failedIds(failedIds)
                .message("Batch processed: " + processed + " ok, " + failedIds.size() + " failed")
                .build();
    }

    private void processSyncItem(SyncRequest item) {
        // Route to appropriate downstream service based on entityType
        // In production this would call the target service via Feign or Kafka
        log.info("Syncing {} {} operation on entity {}",
                item.getOperation(), item.getEntityType(), item.getEntityId());
        item.setStatus("SYNCED");
        syncRequestRepository.save(item);
    }

    public List<SyncRequest> getPendingSyncs(String userId) {
        return syncRequestRepository.findByUserIdAndStatus(userId, "PENDING");
    }

    public SyncRequest getSync(String syncId) {
        return syncRequestRepository.findById(syncId)
                .orElseThrow(() -> new RuntimeException("Sync record not found: " + syncId));
    }

    @Transactional
    public SyncRequest retrySync(String syncId) {
        SyncRequest sync = getSync(syncId);
        sync.setStatus("PENDING");
        sync.setErrorMessage(null);
        sync.setRetryCount(sync.getRetryCount() != null ? sync.getRetryCount() + 1 : 1);
        syncRequestRepository.save(sync);
        processSyncItem(sync);
        return sync;
    }
}
