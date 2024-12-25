package org.team_alilm.quartz.config

import jakarta.annotation.PostConstruct
import org.quartz.Scheduler
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.team_alilm.quartz.scheduler.MemberCountScheduler

@Suppress("SpringJavaInjectionPointsAutowiringInspection")
@Configuration
class MemberCountQuartzConfig(
    private val scheduled: Scheduler,
    @Value("\${spring.quartz.member-count-job-interval-minutes}") private val intervalMinutes: Int
) {

    @PostConstruct
    private fun jobProgress() {
        MemberCountScheduler(scheduled, intervalMinutes).startTracing()
    }
}