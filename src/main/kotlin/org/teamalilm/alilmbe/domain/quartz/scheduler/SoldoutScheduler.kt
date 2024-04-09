package org.teamalilm.alilmbe.domain.quartz.scheduler

import org.quartz.JobBuilder
import org.quartz.Scheduler
import org.quartz.SchedulerException
import org.quartz.SimpleScheduleBuilder
import org.quartz.TriggerBuilder
import org.teamalilm.alilmbe.domain.tracer.SoldoutCheckJob

/**
 *  SoldoutScheduler
 *
 *  @author jubi
 *  @version 1.0.0
 *  @date 2024-03-21
 **/
class SoldoutScheduler(
    val scheduler: Scheduler
) {

    @Throws(SchedulerException::class)
    fun startTracing() {
        val job = JobBuilder.newJob(SoldoutCheckJob::class.java)
            .withIdentity("soldoutCheckJob", "soldoutTracer")
            .build()

        val trigger = TriggerBuilder.newTrigger()
            .withIdentity("soldoutCheckTrigger", "soldoutTracer")
            .startNow()
            .withSchedule(
                SimpleScheduleBuilder.simpleSchedule()
                    .withIntervalInMinutes(1)
                    .repeatForever()
            )
            .build()

        scheduler.scheduleJob(job, trigger)
    }

    companion object {
        const val API_URL_TEMPLATE =
            "https://goods-detail.musinsa.com/goods/%s/options?goodsSaleType=SALE"
    }


}