-- 두 단계 rename: 대소문자만 다른 이름 변경을 모든 OS에서 안전하게 처리
-- 보호 로직: MEMBER가 있고 member는 없을 때만 수행

-- 현재 스키마 기준 존재 여부 체크
SET @has_member       := (SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = 'MEMBER');
SET @has_member_lower := (SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = 'member');

-- MEMBER가 존재하고, member가 아직 없으면 실행
-- ⚠️ Flyway는 기본적으로 여러 문장을 순차 실행합니다.
--    중간 실패 시 MEMBER_TMP로 남을 수 있으니, 아래 “복구 방법” 참고
IF (@has_member = 1 AND @has_member_lower = 0) THEN
  RENAME TABLE `MEMBER` TO `MEMBER_TMP`;
  RENAME TABLE `MEMBER_TMP` TO `member`;
END IF;

-- 필요한 경우 인덱스/제약 확인(예: 고유 인덱스)
-- CREATE UNIQUE INDEX uk_member_provider_pid ON `member` (`provider`, `provider_id`);