package org.teamalilm.alilmbe.global.quartz.scheduler

import org.quartz.JobBuilder
import org.quartz.JobKey
import org.quartz.Scheduler
import org.quartz.SimpleScheduleBuilder
import org.quartz.TriggerBuilder
import org.quartz.TriggerKey
import org.teamalilm.alilmbe.domain.tracer.MusinsaSoldoutCheckJob

/**
 *  SoldoutScheduler
 *
 *  @version 1.0.0
 *  @date 2024-03-21
 **/
class SoldoutScheduler(
    val scheduler: Scheduler
) {

    fun startTracing() {
        val jobKey = JobKey("soldoutCheckJob", "soldoutTracer")
        val triggerKey = TriggerKey("soldoutCheckTrigger", "soldoutTracer")

        // Check if the job already exists
        if (!scheduler.checkExists(jobKey)) {
            val job = JobBuilder.newJob(MusinsaSoldoutCheckJob::class.java)
                .withIdentity(jobKey)
                .build()

            val trigger = TriggerBuilder.newTrigger()
                .withIdentity(triggerKey)
                .startNow()
                .withSchedule(
                    SimpleScheduleBuilder.simpleSchedule()
                        .withIntervalInMinutes(10)
                        .repeatForever()
                )
                .build()

            scheduler.scheduleJob(job, trigger)
        } else {
            println("Job with key $jobKey already exists")
        }
    }

}
