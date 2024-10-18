package org.team_alilm.quartz.job

import com.fasterxml.jackson.databind.ObjectMapper
import org.quartz.Job
import org.quartz.JobExecutionContext
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.client.RestClient
import org.springframework.web.client.RestClientException
import org.springframework.web.client.body
import org.team_alilm.adapter.out.gateway.FcmSendGateway
import org.team_alilm.adapter.out.gateway.JsoupProductDataGateway
import org.team_alilm.adapter.out.gateway.MailGateway
import org.team_alilm.adapter.out.gateway.SlackGateway
import org.team_alilm.application.port.out.*
import org.team_alilm.application.port.out.gateway.CrawlingGateway
import org.team_alilm.global.util.StringConstant
import org.team_alilm.quartz.data.SoldoutCheckResponse
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
    val addBasketPort: AddBasketPort,
    val restClient: RestClient,
    val mailGateway: MailGateway,
    val slackGateway: SlackGateway,
    val sendAlilmBasketPort: SendAlilmBasketPort,
    val jsoupProductDataGateway: JsoupProductDataGateway,
    val fcmSendGateway: FcmSendGateway,
    val loadFcmTokenPort: LoadFcmTokenPort,
) : Job {

    private val log = LoggerFactory.getLogger(MusinsaSoldoutCheckJob::class.java)

    @Transactional
    override fun execute(context: JobExecutionContext) {
        val basketAndMemberAndProducts = loadAllBasketsPort.loadAllBaskets()

        basketAndMemberAndProducts.forEach { basketAndMemberAndProduct ->
            log.info("""
                Checking product: ${basketAndMemberAndProduct.product.number}
                for member: ${basketAndMemberAndProduct.member.nickname}
                
                """.trimIndent())

            val productId = basketAndMemberAndProduct.product.number
            val requestUri = StringConstant.MUSINSA_API_URL_TEMPLATE.get().format(productId)
            val musinsaProductHtmlRequestUrl = StringConstant.MUSINSA_PRODUCT_HTML_REQUEST_URL.get().format(productId)

            // 상품의 전체 품절 시 확인 하는 로직을 가지고 있어요.
            val response = jsoupProductDataGateway.crawling(CrawlingGateway.CrawlingGatewayRequest(musinsaProductHtmlRequestUrl))
            val jsonData = extractJsonData(response.html, "window.__MSS__.product.state")
            val jsonObject = ObjectMapper().readTree(jsonData)

            val isAllSoldout = jsonObject.get("goodsSaleType").toString() == "SOLDOUT"

            val isSoldOut = if (isAllSoldout) {
                true
            } else {
                try {
                    checkIfSoldOut(requestUri, basketAndMemberAndProduct)
                } catch (e: RestClientException) {
                    log.info("Failed to check soldout status of product: $productId")
                    slackGateway.sendMessage("""
                        Failed to check soldout status of 
                        product number : $productId
                        store : musinsa
                        
                        ${e.message}
                    """.trimIndent())
                    true
                }
            }

            if (!isSoldOut) {
                sendNotifications(basketAndMemberAndProduct)
                basketAndMemberAndProduct.basket.sendAlilm()

                addBasketPort.addBasket(
                    basketAndMemberAndProduct.basket,
                    basketAndMemberAndProduct.member,
                    basketAndMemberAndProduct.product
                )

                sendAlilmBasketPort.addAlilmBasket(
                    basketAndMemberAndProduct.basket,
                    basketAndMemberAndProduct.member,
                    basketAndMemberAndProduct.product
                )

                val fcmTokenList = loadFcmTokenPort.loadFcmTokenAllByMember(basketAndMemberAndProduct.member.id!!.value)

                fcmTokenList.forEach() { fcmToken ->
                    fcmSendGateway.sendFcmMessage(
                        member = basketAndMemberAndProduct.member,
                        product = basketAndMemberAndProduct.product,
                        fcmToken = fcmToken
                    )
                }
            }
        }
    }

    private fun sendNotifications(basketAndMemberAndProduct: LoadAllBasketsPort.BasketAndMemberAndProduct) {
        mailGateway.sendMail(
            basketAndMemberAndProduct.member.email,
            basketAndMemberAndProduct.member.nickname,
            basketAndMemberAndProduct.product.number,
            basketAndMemberAndProduct.product.imageUrl,
            basketAndMemberAndProduct.getEmailOption()
        )
        slackGateway.sendMessage(getSlackMessage(basketAndMemberAndProduct))
    }

    private fun checkIfSoldOut(requestUri: String, basketAndMemberAndProduct: LoadAllBasketsPort.BasketAndMemberAndProduct): Boolean {
        val response = restClient.get().uri(requestUri).retrieve().body<SoldoutCheckResponse>()
        val optionItem = response?.data?.optionItems?.firstOrNull {
            it.managedCode == basketAndMemberAndProduct.getManagedCode() }

        return optionItem?.outOfStock ?: true
    }

    private fun getSlackMessage(basketAndMemberAndProduct: LoadAllBasketsPort.BasketAndMemberAndProduct): String {
        return """
            ${basketAndMemberAndProduct.product.name} 상품이 재 입고 되었습니다.
            바구니에서 삭제되었습니다.
        """.trimIndent()
    }

    private fun extractJsonData(scriptContent: String, variableName: String): String? {
        var jsonString: String? = null

        // 자바스크립트 내 변수 선언 패턴
        val pattern = "$variableName = "

        // 패턴의 시작 위치 찾기
        val startIndex = scriptContent.indexOf(pattern)

        if (startIndex != -1) {
            // 패턴 이후 부분 추출
            val substring = scriptContent.substring(startIndex + pattern.length)

            // JSON 데이터의 끝 위치 찾기
            val endIndex = substring.indexOf("};") + 1

            // JSON 문자열 추출
            jsonString = substring.substring(0, endIndex)
        }

        return jsonString
    }

}
