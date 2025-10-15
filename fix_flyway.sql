-- Flyway 스키마 히스토리에서 실패한 V11 마이그레이션 제거
-- 이 SQL을 MySQL 클라이언트에서 실행하세요

USE bell;

-- 실패한 V11 마이그레이션 기록 삭제
DELETE FROM flyway_schema_history WHERE version = '11' AND success = 0;

-- 현재 상태 확인
SELECT * FROM flyway_schema_history ORDER BY installed_rank DESC LIMIT 5;
