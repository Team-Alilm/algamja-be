package org.team_alilm.quartz.config

import jakarta.annotation.PostConstruct
import org.quartz.Scheduler
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.team_alilm.quartz.scheduler.SoldoutScheduler

@Suppress("SpringJavaInjectionPointsAutowiringInspection")
@Configuration
class QuartzConfig(
    val scheduler: Scheduler,
    @Value("\${spring.quartz.job-interval-minutes}") private val intervalMinutes: Int
) {
    @PostConstruct
    private fun jobProgress() {
        SoldoutScheduler(scheduler, intervalMinutes).startTracing()
    }

}

