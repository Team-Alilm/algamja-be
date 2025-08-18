CREATE TABLE member (
                        id BIGINT PRIMARY KEY AUTO_INCREMENT,
                        provider VARCHAR(20) NOT NULL,
                        provider_id VARCHAR(64) NOT NULL,
                        email VARCHAR(255) NOT NULL,
                        nickname VARCHAR(50) NOT NULL,
                        is_delete BOOLEAN NOT NULL DEFAULT FALSE,
                        created_date BIGINT NOT NULL,
                        last_modified_date BIGINT NOT NULL,
                        CONSTRAINT uk_member_provider_pid UNIQUE (provider, provider_id)
);

-- 필요한 인덱스 추가 예시
-- CREATE INDEX idx_member_provider ON member(provider);