CREATE TABLE IF NOT EXISTS sync_requests (
    id                VARCHAR(36) PRIMARY KEY,
    device_id         VARCHAR(100) NOT NULL,
    user_id           VARCHAR(36) NOT NULL,
    entity_type       VARCHAR(50) NOT NULL,
    entity_id         VARCHAR(36) NOT NULL,
    operation         VARCHAR(20) NOT NULL,
    payload           JSONB,
    client_timestamp  BIGINT NOT NULL,
    server_timestamp  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    status            VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    error_message     TEXT,
    retry_count       INTEGER DEFAULT 0
);

CREATE INDEX idx_sync_user_status ON sync_requests(user_id, status);
CREATE INDEX idx_sync_device_status ON sync_requests(device_id, status);
CREATE INDEX idx_sync_entity ON sync_requests(entity_id, entity_type);
