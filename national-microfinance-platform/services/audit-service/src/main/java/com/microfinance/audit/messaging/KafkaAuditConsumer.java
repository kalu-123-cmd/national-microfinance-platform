package com.microfinance.audit.messaging;

import com.microfinance.audit.domain.model.AuditLog;
import com.microfinance.audit.domain.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaAuditConsumer {

    private final AuditLogRepository auditLogRepository;

    @KafkaListener(topics = "audit-events", groupId = "audit-service-group")
    public void consumeAuditEvent(Map<String, Object> payload) {
        log.debug("Received audit event: {}", payload);
        
        try {
            AuditLog auditLog = AuditLog.builder()
                    .id(UUID.randomUUID().toString())
                    .serviceName((String) payload.getOrDefault("serviceName", "UNKNOWN"))
                    .action((String) payload.getOrDefault("action", "UNKNOWN"))
                    .userId((String) payload.get("userId"))
                    .resourceId((String) payload.get("resourceId"))
                    .details((String) payload.get("details"))
                    .ipAddress((String) payload.get("ipAddress"))
                    .status((String) payload.getOrDefault("status", "SUCCESS"))
                    .errorMessage((String) payload.get("errorMessage"))
                    .build();

            auditLogRepository.save(auditLog);
            log.info("Saved audit log for action: {}", auditLog.getAction());
        } catch (Exception e) {
            log.error("Failed to process audit event: {}", e.getMessage(), e);
        }
    }
}
