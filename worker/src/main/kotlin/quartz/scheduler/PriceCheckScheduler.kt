package org.team_alilm.quartz.scheduler

import org.quartz.*
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.team_alilm.quartz.job.MusinsaPriceCheckJob
import org.team_alilm.quartz.listener.PriceQuartzListener

@Component
class PriceCheckScheduler(
    private val scheduler: Scheduler,
    @Value("\${spring.quartz.job-interval-minutes}") // need to change
    private val intervalMinutes: Int
) {
    private val log = LoggerFactory.getLogger(PriceCheckScheduler::class.java)

    fun startTracing() {
        try {
            val jobKey = JobKey.jobKey("priceCheckJob", "priceTracer")
            // Check if the job already exists and delete if necessary
            if (scheduler.checkExists(jobKey)) {
                log.info("Deleting existing job: $jobKey")
                scheduler.deleteJob(jobKey)
            }

            val jobDetail: JobDetail = JobBuilder.newJob(MusinsaPriceCheckJob::class.java)
                .withIdentity("priceCheckJob", "priceTracer")
                .storeDurably()
                .build()

            val trigger: Trigger = TriggerBuilder.newTrigger()
                .forJob(jobDetail)
                .withIdentity("priceCheckTrigger", "priceTracer")
                .withSchedule(
                    SimpleScheduleBuilder.simpleSchedule()
                        .withIntervalInMinutes(intervalMinutes)
                        .repeatForever()
                )
                .build()

            scheduler.scheduleJob(jobDetail, trigger)
            // Register the JobListener
            scheduler.listenerManager.addJobListener(PriceQuartzListener())

            log.info("PriceCheckScheduler setup with job and trigger")
        } catch (e: Exception) {
            log.error("Error setting up PriceCheckScheduler", e)
        }
    }
}