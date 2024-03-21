package org.teamalilm.alilmbe.domain.tracer

import org.quartz.JobExecutionContext
import org.quartz.JobExecutionException
import org.quartz.JobListener
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class SoldoutQuartzListener(
    val log: Logger = LoggerFactory.getLogger(SoldoutQuartzListener::class.java)!!
) : JobListener {

    override fun getName(): String {
        return this.name
    }

    /**
     * Job 실행 이전 수행
     */
    override fun jobToBeExecuted(p0: JobExecutionContext?) {
        log.info("Job is going to be executed")
    }

    /**
     * Job 실행 취소 시점 수행
     */
    override fun jobExecutionVetoed(p0: JobExecutionContext?) {
        log.info("Job execution is vetoed")
    }

    /**
     * Job 실행 완료 시점 수행
     */
    override fun jobWasExecuted(p0: JobExecutionContext?, p1: JobExecutionException?) {
        TODO("Not yet implemented")
    }
}