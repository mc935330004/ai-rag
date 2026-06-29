CREATE TABLE IF NOT EXISTS knowledge_base (
                                              id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                              name VARCHAR(255) NOT NULL,
    category VARCHAR(100),
    original_filename VARCHAR(255) NOT NULL,
    content_type VARCHAR(100),
    file_size BIGINT NOT NULL DEFAULT 0,
    file_hash VARCHAR(128) NOT NULL,
    storage_path VARCHAR(500) NOT NULL,
    vector_status VARCHAR(32) NOT NULL DEFAULT 'PENDING',
    vector_error VARCHAR(500),
    chunk_count INT NOT NULL DEFAULT 0,
    question_count BIGINT NOT NULL DEFAULT 0,
    access_count BIGINT NOT NULL DEFAULT 0,
    last_accessed_at DATETIME,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    del_flag TINYINT NOT NULL DEFAULT 0
    );

CREATE INDEX idx_kb_file_hash
    ON knowledge_base (file_hash);

CREATE INDEX idx_kb_vector_status
    ON knowledge_base (vector_status);

CREATE INDEX idx_kb_category
    ON knowledge_base (category);

CREATE INDEX idx_kb_created_at
    ON knowledge_base (created_at);