-- V3: Add user change history table for tracking profile and measurement changes

CREATE TABLE user_change_history (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    entity_type VARCHAR(20) NOT NULL,
    entity_id BIGINT,
    field_name VARCHAR(50) NOT NULL,
    old_value TEXT,
    new_value TEXT,
    changed_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_change_history_user ON user_change_history(user_id, changed_at DESC);
