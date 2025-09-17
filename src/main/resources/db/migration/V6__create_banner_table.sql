-- Create banner table
CREATE TABLE banner (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(100) NOT NULL COMMENT '배너 제목',
    image_url VARCHAR(512) NOT NULL COMMENT '배너 이미지 URL',
    click_url VARCHAR(512) NULL COMMENT '클릭 시 이동할 URL',
    priority INT NOT NULL DEFAULT 0 COMMENT '우선순위 (높을수록 먼저 노출)',
    start_date BIGINT NOT NULL COMMENT '노출 시작 시간 (밀리초)',
    end_date BIGINT NOT NULL COMMENT '노출 종료 시간 (밀리초)',
    is_active BOOLEAN NOT NULL DEFAULT TRUE COMMENT '활성화 여부',
    is_delete BOOLEAN NOT NULL DEFAULT FALSE COMMENT '삭제 여부',
    created_date BIGINT NOT NULL COMMENT '생성 시간 (밀리초)',
    last_modified_date BIGINT NOT NULL COMMENT '최종 수정 시간 (밀리초)'
);

-- Create indexes
CREATE INDEX idx_banner_priority ON banner(priority);
CREATE INDEX idx_banner_dates ON banner(start_date, end_date);
CREATE INDEX idx_banner_active ON banner(is_active);