-- =========================================================
-- 1. 모든 테이블의 created_date, last_modified_date를 created_at, updated_at으로 변경
-- =========================================================

-- member 테이블
ALTER TABLE `member`
    CHANGE COLUMN `created_date` `created_at` BIGINT NOT NULL,
    CHANGE COLUMN `last_modified_date` `updated_at` BIGINT NOT NULL;

-- product 테이블
ALTER TABLE `product`
    CHANGE COLUMN `created_date` `created_at` BIGINT NOT NULL,
    CHANGE COLUMN `last_modified_date` `updated_at` BIGINT NOT NULL;

-- notification 테이블
ALTER TABLE `notification`
    CHANGE COLUMN `created_date` `created_at` BIGINT NOT NULL,
    CHANGE COLUMN `last_modified_date` `updated_at` BIGINT NOT NULL;

-- fcm_token 테이블
ALTER TABLE `fcm_token`
    CHANGE COLUMN `created_date` `created_at` BIGINT NOT NULL,
    CHANGE COLUMN `last_modified_date` `updated_at` BIGINT NOT NULL;

-- basket 테이블
ALTER TABLE `basket`
    CHANGE COLUMN `created_date` `created_at` BIGINT NOT NULL,
    CHANGE COLUMN `last_modified_date` `updated_at` BIGINT NOT NULL;

-- product_image 테이블
ALTER TABLE `product_image`
    CHANGE COLUMN `created_date` `created_at` BIGINT NOT NULL,
    CHANGE COLUMN `last_modified_date` `updated_at` BIGINT NOT NULL;

-- price_history 테이블
ALTER TABLE `price_history`
    CHANGE COLUMN `created_date` `created_at` BIGINT NOT NULL,
    CHANGE COLUMN `last_modified_date` `updated_at` BIGINT NOT NULL;

-- banner 테이블 (V6에서 생성됨)
ALTER TABLE `banner`
    CHANGE COLUMN `created_date` `created_at` BIGINT NOT NULL,
    CHANGE COLUMN `last_modified_date` `updated_at` BIGINT NOT NULL;

-- =========================================================
-- 2. product_price_history 테이블에 is_delete 컬럼 추가
-- =========================================================

ALTER TABLE `product_price_history`
    ADD COLUMN `is_delete` BOOLEAN NOT NULL DEFAULT FALSE COMMENT '소프트 삭제 여부' AFTER `id`;
