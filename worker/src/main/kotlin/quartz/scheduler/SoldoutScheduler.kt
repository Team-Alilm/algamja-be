package org.team_alilm.quartz.scheduler

import org.quartz.JobBuilder
import org.quartz.JobDetail
import org.quartz.Trigger
import org.quartz.TriggerBuilder
import org.quartz.SimpleScheduleBuilder
import org.quartz.Scheduler
import org.quartz.JobKey
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component
import org.team_alilm.quartz.job.MusinsaSoldoutCheckJob
import org.team_alilm.quartz.listener.SoldoutQuartzListener

/**
 * SoldoutScheduler
 *
 * @version 1.0.0
 * @date 2024-03-21
 **/
@Component
class SoldoutScheduler(
    private val scheduler: Scheduler,
    @Value("\${spring.quartz.job-interval-minutes}")
    private val intervalMinutes: Int
) {
    private val log = LoggerFactory.getLogger(SoldoutScheduler::class.java)

    fun startTracing() {
        try {
            val jobKey = JobKey.jobKey("soldoutCheckJob", "soldoutTracer")
            // Check if the job already exists and delete if necessary
            if (scheduler.checkExists(jobKey)) {
                log.info("Deleting existing job: $jobKey")
                scheduler.deleteJob(jobKey)
            }

            val jobDetail: JobDetail = JobBuilder.newJob(MusinsaSoldoutCheckJob::class.java)
                .withIdentity("soldoutCheckJob", "soldoutTracer")
                .storeDurably()
                .build()

            val trigger: Trigger = TriggerBuilder.newTrigger()
                .forJob(jobDetail)
                .withIdentity("soldoutCheckTrigger", "soldoutTracer")
                .withSchedule(
                    SimpleScheduleBuilder.simpleSchedule()
                        .withIntervalInMinutes(intervalMinutes)
                        .repeatForever()
                )
                .build()

            scheduler.scheduleJob(jobDetail, trigger)
            // Register the JobListener
            scheduler.listenerManager.addJobListener(SoldoutQuartzListener())

            log.info("Scheduler setup with job and trigger")
        } catch (e: Exception) {
            log.error("Error setting up scheduler", e)
        }
    }
}
