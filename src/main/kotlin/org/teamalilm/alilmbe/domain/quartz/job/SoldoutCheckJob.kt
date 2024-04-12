package org.teamalilm.alilmbe.domain.tracer

import java.net.URI
import org.quartz.Job
import org.quartz.JobExecutionContext
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.client.RestClient
import org.springframework.web.client.body
import org.teamalilm.alilmbe.domain.basket.repository.BasketRepository
import org.teamalilm.alilmbe.domain.quartz.data.SoldoutCheckResponse
import org.teamalilm.alilmbe.domain.quartz.scheduler.SoldoutScheduler
import org.teamalilm.alilmbe.global.email.data.EmailMessage
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

        val restockProductIds = mutableListOf<Long>()
        val restClient = RestClient.create()
        val baskets = basketRepository.findAllByGroupByProductId()

        baskets.forEach {
            log.info("상품 ID: ${it.product.id}")

            val apiUrl = SoldoutScheduler.API_URL_TEMPLATE.format(it.product.productInfo.number)

            val response = restClient.get()
                .uri(URI.create(apiUrl))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body<SoldoutCheckResponse>()

            val option1 = it.product.productInfo.option1
            val option2 = it.product.productInfo.option2

            val isSoldOut =
                response?.data?.basic?.firstOrNull { item -> item.name == option1 }
                    ?.run {
                        if (subOptions.isNotEmpty()) {
                            subOptions.find { subOption -> subOption.name == option2 }?.run {
                                isSoldOut
                            }
                                ?: throw IllegalStateException("상품 옵션2을 찾지 못했어요. 상품번호: ${it.product.productInfo.number} 옵션1: $option1")
                        } else {
                            isSoldOut
                        }
                    }
                    ?: throw IllegalStateException("상품 옵션1을 찾지 못했어요. 상품번호: ${it.product.productInfo.number} 옵션1: $option1")

            if (!isSoldOut) {
                restockProductIds.add(it.id!!)
            }
        }

        restockProductIds.forEach {
            log.info("재입고 상품 ID: $it")

            val baskets = basketRepository.findAllByProductId(it)
                ?: throw IllegalStateException("해당 상품을 찾을 수 없어요. 상품 ID: $it")

            baskets.forEach {
                val emailMessage = EmailMessage(
                    from = "Alilm",
                    to = "cloudwi@naver.com",
                    subject = "재입고 알림",
                    text = "${it.product.name} 상품이 재입고 되었습니다. 확인해주세요.",
                )

                basketRepository.deleteById(it.id!!)
                emailService.sendMail(emailMessage)
                slackService.sendSlackMessage("${it.product.name} 상품이 재입고 되었습니다. 확인해주세요.")
            }

        }
    }
}