package org.teamalilm.alilmbe.common.config

import jakarta.annotation.PostConstruct
import org.quartz.Scheduler
import org.springframework.context.annotation.Configuration
import org.teamalilm.alilmbe.global.quartz.scheduler.SoldoutScheduler


/**
 *  QuartzConfig
 *
 *  @author jubi
 *  @version 1.0.0
 *  @date 2024-03-21
 **/
@Configuration
class QuartzConfig(
    val scheduler: Scheduler
) {
    @PostConstruct
    private fun jobProgress() {
        SoldoutScheduler(scheduler).startTracing()
    }

}