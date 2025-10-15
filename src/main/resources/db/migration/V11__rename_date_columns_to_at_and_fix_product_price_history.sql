-- =========================================================
-- 1. 모든 테이블의 created_date, last_modified_date를 created_at, updated_at으로 변경
-- =========================================================

-- member 테이블
SET @column_exists = (
    SELECT COUNT(*)
    FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'member'
      AND COLUMN_NAME = 'created_date'
);

SET @sql = IF(@column_exists > 0,
              'ALTER TABLE `member` CHANGE COLUMN `created_date` `created_at` BIGINT NOT NULL, CHANGE COLUMN `last_modified_date` `updated_at` BIGINT NOT NULL',
              'SELECT "Columns already renamed in member" AS message');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- product 테이블
SET @column_exists = (
    SELECT COUNT(*)
    FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'product'
      AND COLUMN_NAME = 'created_date'
);

SET @sql = IF(@column_exists > 0,
              'ALTER TABLE `product` CHANGE COLUMN `created_date` `created_at` BIGINT NOT NULL, CHANGE COLUMN `last_modified_date` `updated_at` BIGINT NOT NULL',
              'SELECT "Columns already renamed in product" AS message');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- notification 테이블
SET @column_exists = (
    SELECT COUNT(*)
    FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'notification'
      AND COLUMN_NAME = 'created_date'
);

SET @sql = IF(@column_exists > 0,
              'ALTER TABLE `notification` CHANGE COLUMN `created_date` `created_at` BIGINT NOT NULL, CHANGE COLUMN `last_modified_date` `updated_at` BIGINT NOT NULL',
              'SELECT "Columns already renamed in notification" AS message');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- fcm_token 테이블
SET @column_exists = (
    SELECT COUNT(*)
    FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'fcm_token'
      AND COLUMN_NAME = 'created_date'
);

SET @sql = IF(@column_exists > 0,
              'ALTER TABLE `fcm_token` CHANGE COLUMN `created_date` `created_at` BIGINT NOT NULL, CHANGE COLUMN `last_modified_date` `updated_at` BIGINT NOT NULL',
              'SELECT "Columns already renamed in fcm_token" AS message');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- basket 테이블
SET @column_exists = (
    SELECT COUNT(*)
    FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'basket'
      AND COLUMN_NAME = 'created_date'
);

SET @sql = IF(@column_exists > 0,
              'ALTER TABLE `basket` CHANGE COLUMN `created_date` `created_at` BIGINT NOT NULL, CHANGE COLUMN `last_modified_date` `updated_at` BIGINT NOT NULL',
              'SELECT "Columns already renamed in basket" AS message');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- product_image 테이블
SET @column_exists = (
    SELECT COUNT(*)
    FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'product_image'
      AND COLUMN_NAME = 'created_date'
);

SET @sql = IF(@column_exists > 0,
              'ALTER TABLE `product_image` CHANGE COLUMN `created_date` `created_at` BIGINT NOT NULL, CHANGE COLUMN `last_modified_date` `updated_at` BIGINT NOT NULL',
              'SELECT "Columns already renamed in product_image" AS message');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- price_history 테이블
SET @column_exists = (
    SELECT COUNT(*)
    FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'price_history'
      AND COLUMN_NAME = 'created_date'
);

SET @sql = IF(@column_exists > 0,
              'ALTER TABLE `price_history` CHANGE COLUMN `created_date` `created_at` BIGINT NOT NULL, CHANGE COLUMN `last_modified_date` `updated_at` BIGINT NOT NULL',
              'SELECT "Columns already renamed in price_history" AS message');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- banner 테이블 (V6에서 생성됨)
SET @column_exists = (
    SELECT COUNT(*)
    FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'banner'
      AND COLUMN_NAME = 'created_date'
);

SET @sql = IF(@column_exists > 0,
              'ALTER TABLE `banner` CHANGE COLUMN `created_date` `created_at` BIGINT NOT NULL, CHANGE COLUMN `last_modified_date` `updated_at` BIGINT NOT NULL',
              'SELECT "Columns already renamed in banner" AS message');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- =========================================================
-- 2. product_price_history 테이블에 is_delete 컬럼 추가
-- =========================================================

SET @column_exists = (
    SELECT COUNT(*)
    FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'product_price_history'
      AND COLUMN_NAME = 'is_delete'
);

SET @sql = IF(@column_exists = 0,
              'ALTER TABLE `product_price_history` ADD COLUMN `is_delete` BOOLEAN NOT NULL DEFAULT FALSE COMMENT ''소프트 삭제 여부'' AFTER `id`',
              'SELECT "Column is_delete already exists in product_price_history" AS message');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
