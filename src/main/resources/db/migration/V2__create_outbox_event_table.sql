CREATE TABLE outbox_event (
                              id UUID PRIMARY KEY,
                              aggregate_id UUID NOT NULL,
                              event_type VARCHAR(100) NOT NULL,
                              payload TEXT NOT NULL,
                              status VARCHAR(30) NOT NULL,
                              created_at TIMESTAMPTZ NOT NULL,
                              published_at TIMESTAMPTZ NULL,
                              retry_count INTEGER NOT NULL DEFAULT 0,

                              CONSTRAINT chk_outbox_event_status CHECK (
                                  status IN ('PENDENTE', 'PUBLICADO', 'FALHA')
                                  ),
                              CONSTRAINT chk_outbox_event_retry_count CHECK (retry_count >= 0)
);

CREATE INDEX idx_outbox_event_status_created_at
    ON outbox_event (status, created_at);

CREATE INDEX idx_outbox_event_aggregate_id
    ON outbox_event (aggregate_id);
