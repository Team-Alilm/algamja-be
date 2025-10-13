-- 상품 가격 히스토리 테이블 생성
CREATE TABLE product_price_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT NOT NULL COMMENT '상품 ID',
    price DECIMAL(15, 0) NOT NULL COMMENT '가격 (원 단위)',
    recorded_at BIGINT NOT NULL COMMENT '기록 시점 (timestamp)',
    created_at BIGINT NOT NULL DEFAULT (UNIX_TIMESTAMP() * 1000) COMMENT '생성 시간',
    updated_at BIGINT NOT NULL DEFAULT (UNIX_TIMESTAMP() * 1000) COMMENT '수정 시간',

    -- 인덱스
    INDEX idx_product_price_history_product_id (product_id),
    INDEX idx_product_price_history_product_time (product_id, recorded_at),

    -- 제약 조건
    CONSTRAINT chk_product_price_history_price_non_negative CHECK (price >= 0),
    CONSTRAINT fk_product_price_history_product FOREIGN KEY (product_id) REFERENCES product(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='상품 가격 변동 히스토리';
