-- Outbox pattern for cache invalidation
CREATE TABLE IF NOT EXISTS outbox_events (
    id UUID PRIMARY KEY,
    aggregate_id UUID NOT NULL,
    aggregate_type VARCHAR(50) NOT NULL,
    event_type VARCHAR(50) NOT NULL,
    payload JSONB NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    processed_at TIMESTAMP,
    retry_count INTEGER DEFAULT 0,
    status VARCHAR(20) DEFAULT 'PENDING',
    CONSTRAINT chk_outbox_status CHECK (status IN ('PENDING', 'PROCESSED', 'FAILED'))
);

-- Index for performance
CREATE INDEX IF NOT EXISTS idx_outbox_events_status ON outbox_events(status, created_at);
