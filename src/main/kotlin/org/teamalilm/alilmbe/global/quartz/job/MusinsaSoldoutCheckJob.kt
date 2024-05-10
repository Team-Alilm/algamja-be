package org.teamalilm.alilmbe.domain.tracer

import org.quartz.Job
import org.quartz.JobExecutionContext
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.client.RestClient
import org.springframework.web.client.body
import org.teamalilm.alilmbe.domain.basket.entity.Basket
import org.teamalilm.alilmbe.domain.basket.repository.BasketRepository
import org.teamalilm.alilmbe.global.email.service.EmailService
import org.teamalilm.alilmbe.global.quartz.data.SoldoutCheckResponse
import org.teamalilm.alilmbe.global.slack.service.SlackService

/**
 *  재고가 없는 상품을 체크하는 Job
 *  재고가 있다면 사용자에게 메세지를 보내고 해당 바구니를 삭제한다.
 **/
@Component
@Transactional(readOnly = true)
class MusinsaSoldoutCheckJob(
    val basketRepository: BasketRepository,
    val emailService: EmailService,
    val slackService: SlackService,
) : Job {

    private val log = LoggerFactory.getLogger(MusinsaSoldoutCheckJob::class.java)

    @Transactional
    override fun execute(context: JobExecutionContext) {
        val baskets = basketRepository.findAll()
        val restClient = RestClient.create()
        val passList = ArrayList<Long>()

        baskets.forEach {
            if (passList.contains(it.product.id)) {
                return@forEach
            }

            val requestUri = MUSINSA_API_URL_TEMPLATE.format(it.product.productInfo.number)

            val response = restClient.get()
                .uri(requestUri)
                .retrieve()
                .body<SoldoutCheckResponse>()

            val isSoldOut =
                response?.data?.basic?.firstOrNull { item -> item.name == it.product.productInfo.option1 }
                    ?.run {
                        if (subOptions.isNotEmpty()) {
                            subOptions.find { subOption -> subOption.name == it.product.productInfo.option2 }
                                ?.run {
                                    isSoldOut
                                }
                                ?: throw IllegalStateException("상품 옵션2을 찾지 못했어요. 상품번호: ${it.product.productInfo.number} 옵션1: ${it.product.productInfo.option1}")
                        } else {
                            isSoldOut
                        }
                    }
                    ?: throw IllegalStateException("상품 옵션1을 찾지 못했어요. 상품번호: ${it.product.productInfo.number} 옵션1: ${it.product.productInfo.option2}")

            if (!isSoldOut) {
                passList.add(it.product.id!!)

                basketRepository.findAllByProductId(it.product.id).forEach {
                    emailService.sendMail(getEmailMessage(it), it.member.email)
                    slackService.sendSlackMessage(getSlackMessage(it))
                    basketRepository.delete(it)
                }
            }
        }
    }

    private fun getEmailMessage(basket: Basket): String {
        return """
            <html>
            <body>
                <h2>안녕하세요 ${basket.member.nickname}님 </h2>
                <p>${basket.product.name} 상품이 재입고 되었어요.</p>
                
                <p>이쁘게 만들어 주실 수 있나요 ?</p>
            </body>
            </html>
        """.trimIndent()
    }


    private fun getSlackMessage(basket: Basket): String {
        return """
            ${basket.product.name} 상품이 재 입고 되었습니다.
            바구니에서 삭제되었습니다.
        """.trimIndent()
    }

    companion object {
        const val MUSINSA_API_URL_TEMPLATE =
            "https://goods-detail.musinsa.com/goods/%s/options?goodsSaleType=SALE"
    }
}