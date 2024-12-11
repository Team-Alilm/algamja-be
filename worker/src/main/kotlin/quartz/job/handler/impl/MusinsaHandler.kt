package org.team_alilm.quartz.job.handler.impl

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import org.springframework.web.client.RestClientException
import org.springframework.web.client.body
import org.team_alilm.adapter.out.gateway.FcmSendGateway
import org.team_alilm.application.port.out.AddBasketPort
import org.team_alilm.application.port.out.LoadBasketAndMemberPort
import org.team_alilm.application.port.out.gateway.crawling.CrawlingGateway
import org.team_alilm.application.port.out.gateway.SendMailGateway
import org.team_alilm.application.port.out.gateway.SendSlackGateway
import org.team_alilm.application.port.out.gateway.crawling.CrawlingGatewayResolver
import org.team_alilm.domain.FcmToken
import org.team_alilm.domain.product.Product
import org.team_alilm.global.util.StringConstant
import org.team_alilm.quartz.data.SoldoutCheckResponse
import org.team_alilm.quartz.job.SoldoutCheckJob
import org.team_alilm.quartz.job.handler.PlatformHandler

@Component
class MusinsaHandler(
    private val objectMapper: ObjectMapper,
    private val restClient: RestClient,
    private val sendSlackGateway: SendSlackGateway,
    private val sendMailGateway: SendMailGateway,
    private val crawlingGatewayResolver: CrawlingGatewayResolver,
    private val loadBasketAndMemberPort: LoadBasketAndMemberPort,
    private val fcmSendGateway: FcmSendGateway,
    private val addBasketPort: AddBasketPort
) : PlatformHandler {

    private val log = LoggerFactory.getLogger(SoldoutCheckJob::class.java)

    override fun process(product: Product) {
        if(checkSoldOut(product).not()) {
            sendNotifications(product)
        }
    }

    private fun checkSoldOut(product: Product): Boolean {
        val musinsaProductHtmlRequestUrl = StringConstant.MUSINSA_PRODUCT_HTML_REQUEST_URL.get().format(product.number)

        // HTML 크롤링을 통한 품절 확인
        val crawlingGateway = crawlingGatewayResolver.resolve(product.store)
        val response = crawlingGateway.crawling(CrawlingGateway.CrawlingGatewayRequest(musinsaProductHtmlRequestUrl))
        val jsonData = extractJsonData(response.html)

        return if (jsonData != null) {
            // JSON 데이터 파싱
            val jsonObject = objectMapper.readTree(jsonData)
            val isGoodsSaleTypeEqualsSALE = jsonObject.get("goodsSaleType").toString() == "\"SALE\""

            if (isGoodsSaleTypeEqualsSALE.not()) {
                true
            } else {
                // API 호출로 재확인
                val requestUri = StringConstant.MUSINSA_API_URL_TEMPLATE.get().format(product.number)
                try {
                    checkIfSoldOut(requestUri, product)
                } catch (e: RestClientException) {
                    log.error("Failed to check soldout status of product: ${product.number}", e)
                    sendSlackGateway.sendMessage("무신사 서버에 요청 시 에러가 발생했어요.: ${product.number}\nError: ${e.message}")
                    true
                }
            }
        } else {
            log.error("No JSON data found for product: ${product.number}")
            val requestUri = StringConstant.MUSINSA_API_URL_TEMPLATE.get().format(product.number)

            try {
                checkIfSoldOut(requestUri, product)
            } catch (e: RestClientException) {
                log.error("Failed to check soldout status of product: ${product.number}", e)
                sendSlackGateway.sendMessage("무신사 서버에 요청 시 에러가 발생했어요.: ${product.number}\nError: ${e.message}")
                true
            }
        }
    }

    private fun extractJsonData(scriptContent: String): String? {
        var jsonString: String? = null

        // 자바스크립트 내 변수 선언 패턴
        val pattern = "window.__MSS__.product.state = "
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

    private fun checkIfSoldOut(requestUri: String, product: Product): Boolean {
        val response = restClient.get().uri(requestUri).retrieve().body<SoldoutCheckResponse>()
        val optionItem = response?.data?.optionItems?.firstOrNull {
            it.managedCode == product.getManagedCode() }

        return optionItem?.outOfStock ?: true
    }

    private fun sendNotifications(product: Product) {
        val basketAndMemberList = loadBasketAndMemberPort.loadBasketAndMember(product)

        basketAndMemberList.forEach { (basket, member, fcmToken) ->
            sendSlackGateway.sendMessage(product)
            sendMailGateway.sendMail(member.email, member.nickname, product)
            fcmSendGateway.sendFcmMessage(member = member, fcmToken = fcmToken, product = product)
            basket.sendAlilm()
            addBasketPort.addBasket(basket, memberId = member.id!!, productId = product.id!!)
        }
    }
}