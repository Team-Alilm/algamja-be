package org.team_alilm.global.config

import jakarta.annotation.PostConstruct
import org.quartz.Scheduler
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.teamalilm.alilm.global.quartz.scheduler.SoldoutScheduler


/**
 *  QuartzConfig
 *
 *  @author jubi
 *  @version 1.0.0
 *  @date 2024-03-21
 **/
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