package org.team_alilm.global.quartz.listener

import org.quartz.JobExecutionContext
import org.quartz.JobExecutionException
import org.quartz.JobListener
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * SoldoutQuartzListener
 *
 * @version 1.0.0
 * @date 2024-03-21
 **/
class SoldoutQuartzListener(
    private val log: Logger = LoggerFactory.getLogger(SoldoutQuartzListener::class.java)
) : JobListener {

    override fun getName(): String {
        return "SoldoutQuartzListener"
    }

    /**
     * Job 실행 이전 수행
     */
    override fun jobToBeExecuted(context: JobExecutionContext?) {
        log.info("Job is going to be executed")
    }

    /**
     * Job 실행 취소 시점 수행
     */
    override fun jobExecutionVetoed(context: JobExecutionContext?) {
        log.info("Job execution is vetoed")
    }

    /**
     * Job 실행 완료 시점 수행
     */
    override fun jobWasExecuted(context: JobExecutionContext?, exception: JobExecutionException?) {
        log.info("Job was executed")
        if (exception != null) {
            log.error("Job execution failed", exception)
        }
    }
}
