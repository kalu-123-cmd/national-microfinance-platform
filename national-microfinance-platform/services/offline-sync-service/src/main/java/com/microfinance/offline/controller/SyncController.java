package com.microfinance.offline.controller;

import com.microfinance.offline.domain.model.SyncRequest;
import com.microfinance.offline.dto.SyncBatchRequest;
import com.microfinance.offline.dto.SyncResponse;
import com.microfinance.offline.service.SyncService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/sync")
@RequiredArgsConstructor
public class SyncController {

    private final SyncService syncService;

    @PostMapping("/upload")
    public ResponseEntity<SyncResponse> uploadBatch(@RequestBody SyncBatchRequest batch) {
        return ResponseEntity.ok(syncService.processBatch(batch));
    }

    @GetMapping("/pending")
    public ResponseEntity<List<SyncRequest>> getPending(@RequestParam String userId) {
        return ResponseEntity.ok(syncService.getPendingSyncs(userId));
    }

    @GetMapping("/status/{syncId}")
    public ResponseEntity<SyncRequest> getStatus(@PathVariable String syncId) {
        return ResponseEntity.ok(syncService.getSync(syncId));
    }

    @PostMapping("/retry/{syncId}")
    public ResponseEntity<SyncRequest> retrySync(@PathVariable String syncId) {
        return ResponseEntity.ok(syncService.retrySync(syncId));
    }
}
