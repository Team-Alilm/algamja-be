package org.team_alilm.common.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableAsync
import org.team_alilm.common.log.MdcTaskDecorator

@Configuration
@EnableAsync
class AsyncConfig {

    @Bean
    fun mdcTaskDecorator(): MdcTaskDecorator = MdcTaskDecorator()
}