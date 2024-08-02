package org.teamalilm.alilmbe.common.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.domain.AuditorAware
import org.teamalilm.alilmbe.global.jpa.config.AuditorAwareImpl


@Configuration
class JpaAuditorAwareConfig {

    @Bean
    fun auditorAware(): AuditorAware<String> {
        return AuditorAwareImpl()
    }
}