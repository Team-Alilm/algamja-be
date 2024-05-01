package org.teamalilm.alilmbe.domain.tracer

import org.quartz.Job
import org.quartz.JobExecutionContext
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import org.teamalilm.alilmbe.domain.basket.repository.BasketRepository
import org.teamalilm.alilmbe.global.email.service.EmailService
import org.teamalilm.alilmbe.global.slack.service.SlackService

/**
 *  SoldoutCheckJob
 *
 *  @author jubi
 *  @version 1.0.0
 *  @date 2024-03-21
 **/
@Component
@Transactional(readOnly = true)
class SoldoutCheckJob(
    val basketRepository: BasketRepository,
    val emailService: EmailService,
    val slackService: SlackService
) : Job {

    private val log = LoggerFactory.getLogger(SoldoutCheckJob::class.java)

    @Transactional
    override fun execute(context: JobExecutionContext) {
        log.info("SoldoutCheckJob is running")


    }
}