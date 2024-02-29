package org.teamalilm.alilmbe.global.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.domain.AuditorAware
import java.util.*

@Configuration
class AuditingConfiguration {

    @Bean
    fun auditorAware() = AuditorAware { Optional.of("DDD")}
}