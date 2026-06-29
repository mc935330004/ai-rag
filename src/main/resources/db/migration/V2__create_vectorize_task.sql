CREATE TABLE IF NOT EXISTS knowledge_base_vector_task (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    knowledge_base_id BIGINT NOT NULL,
    task_type VARCHAR(32) NOT NULL DEFAULT 'VECTORIZE',
    status VARCHAR(32) NOT NULL DEFAULT 'PENDING',
    retry_count INT NOT NULL DEFAULT 0,
    max_retry_count INT NOT NULL DEFAULT 3,
    lock_owner VARCHAR(100),
    locked_at DATETIME,
    error_message VARCHAR(500),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    started_at DATETIME,
    finished_at DATETIME
    );

CREATE INDEX idx_vector_task_status_created
    ON knowledge_base_vector_task (status, created_at);

CREATE INDEX idx_vector_task_kb_id
    ON knowledge_base_vector_task (knowledge_base_id);

CREATE INDEX idx_vector_task_lock
    ON knowledge_base_vector_task (lock_owner, locked_at);