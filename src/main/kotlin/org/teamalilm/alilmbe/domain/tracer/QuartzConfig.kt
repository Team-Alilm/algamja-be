package org.teamalilm.alilmbe.domain.tracer

import jakarta.annotation.PostConstruct
import org.quartz.Scheduler
import org.quartz.SchedulerException
import org.springframework.context.annotation.Configuration


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
    @Throws(SchedulerException::class)
    private fun jobProgress() {
        SoldoutScheduler(scheduler).startTracing()
    }

}