-- Skip rename when only `member` exists

-- 존재여부(대소문자 구분) 확인
SET @has_member_lower := (
    SELECT COUNT(*) FROM information_schema.tables
    WHERE table_schema = DATABASE() AND BINARY table_name = 'member'
);
SET @has_MEMBER := (
    SELECT COUNT(*) FROM information_schema.tables
    WHERE table_schema = DATABASE() AND BINARY table_name = 'MEMBER'
);

-- 이미 `member`가 있으면 끝 (no-op)
-- (Flyway는 여러 문장을 순차 실행하므로, 조건만 확인하고 종료)
-- 주의: 여기서는 실제 작업이 없으니, 스크립트 통과 = “건너뜀”
DO 0 + @has_member_lower; -- 더미 연산(실행 로그 남기는 용도)