package org.teamalilm.alilm.common.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.domain.AuditorAware
import org.teamalilm.alilm.global.jpa.config.AuditorAwareImpl


@Configuration
class JpaAuditorAwareConfig {

    @Bean
    fun auditorAware(): AuditorAware<String> {
        return AuditorAwareImpl()
    }
}