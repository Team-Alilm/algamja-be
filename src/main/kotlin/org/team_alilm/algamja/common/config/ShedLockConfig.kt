package org.team_alilm.algamja.common.config

import net.javacrumbs.shedlock.core.LockProvider
import net.javacrumbs.shedlock.provider.jdbctemplate.JdbcTemplateLockProvider
import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.JdbcTemplate
import javax.sql.DataSource

/**
 * ShedLock 설정
 * 
 * 분산 환경에서 스케줄러의 중복 실행을 방지합니다.
 * - 동일한 스케줄러가 여러 인스턴스에서 동시에 실행되는 것을 방지
 * - DB 기반 락을 사용하여 안전한 분산 스케줄링 보장
 */
// @Configuration
// @EnableSchedulerLock(defaultLockAtMostFor = "30m") // 기본 최대 락 시간 30분
class ShedLockConfig {

    /**
     * JDBC Template 기반 락 제공자 설정
     * 기존 데이터베이스를 사용하므로 추가 인프라 불필요
     * 
     * TEMPORARILY DISABLED due to MySQL table creation issues
     */
    // @Bean
    fun lockProvider(dataSource: DataSource): LockProvider {
        return JdbcTemplateLockProvider(
            JdbcTemplateLockProvider.Configuration.builder()
                .withJdbcTemplate(JdbcTemplate(dataSource))
                .withTableName("shedlock") // V6 마이그레이션에서 생성한 테이블
                .build()
        )
    }
}