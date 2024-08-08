package org.teamalilm.alilmbe.global.quartz.job

import org.quartz.Job
import org.quartz.JobExecutionContext
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.client.RestClient
import org.springframework.web.client.RestClientException
import org.springframework.web.client.body
import org.teamalilm.alilmbe.adapter.out.gateway.MailGateway
import org.teamalilm.alilmbe.application.port.out.LoadAllBasketsPort
import org.teamalilm.alilmbe.application.port.out.UpdateBasketPort
import org.teamalilm.alilmbe.global.quartz.data.SoldoutCheckResponse
import org.teamalilm.alilmbe.global.slack.service.SlackService
import java.time.LocalDateTime
import java.time.ZoneId

/**
 *  재고가 없는 상품을 체크하는 Job
 *  재고가 있다면 사용자에게 메세지를 보내고 해당 바구니를 삭제한다.
 *  한국 기준 시간을 사용하고 있습니다.
 **/
@Component
@Transactional(readOnly = true)
class MusinsaSoldoutCheckJob(
    val loadAllBasketsPort: LoadAllBasketsPort,
    val restClient: RestClient,
    val emailService: MailGateway,
    val slackService: SlackService,
    val updateBasketPort: UpdateBasketPort
) : Job {

    private val log = LoggerFactory.getLogger(MusinsaSoldoutCheckJob::class.java)

    @Transactional
    override fun execute(context: JobExecutionContext) {
        val baskets = loadAllBasketsPort.loadAllBaskets()

        baskets.forEach { basketAndMemberAndProduct ->
            val productId = basketAndMemberAndProduct.product.id ?: return@forEach
            val requestUri = MUSINSA_API_URL_TEMPLATE.format(basketAndMemberAndProduct.product.number)

            val isSoldOut = try {
                checkIfSoldOut(requestUri, basketAndMemberAndProduct)
            } catch (e: RestClientException) {
                log.info("Failed to check soldout status of product: $productId")
                true
            }

            if (!isSoldOut) {
                sendNotifications(basketAndMemberAndProduct)
                updateBasketPort.deleteBasket(basketAndMemberAndProduct.basket.id!!)
            }
        }
    }

    private fun sendNotifications(basketAndMemberAndProduct: LoadAllBasketsPort.BasketAndMemberAndProduct) {
        emailService.sendMail(getEmailMessage(basketAndMemberAndProduct), basketAndMemberAndProduct.member.email)
        slackService.sendSlackMessage(getSlackMessage(basketAndMemberAndProduct))
    }

    private fun checkIfSoldOut(requestUri: String, basketAndMemberAndProduct: LoadAllBasketsPort.BasketAndMemberAndProduct): Boolean {
        val response = restClient.get().uri(requestUri).retrieve().body<SoldoutCheckResponse>()
        val basicOption = response?.data?.basic?.firstOrNull { item -> item.name == basketAndMemberAndProduct.product.option1 }
        return basicOption?.subOptions?.any { subOption ->
            subOption.name == basketAndMemberAndProduct.product.option2 &&
                    subOption.subOptions.any { it.name == basketAndMemberAndProduct.product.option3 }
        } ?: true
    }

    private fun getEmailMessage(basketAndMemberAndProduct: LoadAllBasketsPort.BasketAndMemberAndProduct): String {
        return """
            <html>
                <body>
                    <h1>Alilm</h1>
                    <div style="width:580px; height:252px; background-color: #F3F3F3; display: flex; flex-direction: column; gap: 40px;">
                        <div style="display: flex; flex-direction: column;">
                            <h2>${basketAndMemberAndProduct.member.nickname}님이 등록하신 제품이</h2>
                            <h2>재입고 되었습니다!</h2>
                        </div>
                        <div style="display: flex; gap: 12px;">
                            <img src="${basketAndMemberAndProduct.product.imageUrl} width="68px" height="80px" />
                            <div>
                                <p>상품 옵션 : ${basketAndMemberAndProduct.product.name}//</p>
                                <p>재입고 시각 : ${LocalDateTime.now().atZone(ZoneId.of("Asia/Seoul")).toLocalDateTime()}</p>
                            </div>
                        </div>
                    </div>
                    <div>
                        <p>${basketAndMemberAndProduct.member.nickname}님이 등록하신 상품의 재입고 소식을 알려드리러 왔어요.</p>
                        <p>상품은 재입고 시각으로 부터 다시 품절이 될 수 있음을 유의해주세요!</p>
                        <p>저희 알림 서비스를 이용해주셔서 감사합니다 :) </p>
                    </div>
                   <a href="https://www.musinsa.com/app/goods/${basketAndMemberAndProduct.product.number}" style="display: inline-block; width: 580px; height: 252px; background-color: #1B1A3B; text-align: center; color: white; text-decoration: none; line-height: 252px;">
                    <h2>재입고 상품 구매하러 가기 👉</h2>
                </a>
                </body>
            </html>

        """.trimIndent()
    }

    private fun getSlackMessage(basketAndMemberAndProduct: LoadAllBasketsPort.BasketAndMemberAndProduct): String {
        return """
            ${basketAndMemberAndProduct.product.name} 상품이 재 입고 되었습니다.
            바구니에서 삭제되었습니다.
        """.trimIndent()
    }

    companion object {
        const val MUSINSA_API_URL_TEMPLATE =
            "https://goods-detail.musinsa.com/goods/%s/options?goodsSaleType=SALE"
    }
}