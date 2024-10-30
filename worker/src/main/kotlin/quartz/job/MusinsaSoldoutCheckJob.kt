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
import org.team_alilm.application.port.out.*
import org.team_alilm.application.port.out.gateway.CrawlingGateway
import org.team_alilm.application.port.out.gateway.SendSlackGateway
import org.team_alilm.domain.Alilm
import org.team_alilm.domain.Member
import org.team_alilm.domain.Product
import org.team_alilm.global.error.NotFoundMemberException
import org.team_alilm.global.util.StringConstant
import org.team_alilm.quartz.data.SoldoutCheckResponse

/**
 *  재고가 없는 상품을 체크하는 Job
 *  재고가 있다면 사용자에게 메세지를 보내고 해당 바구니를 삭제한다.
 *  한국 기준 시간을 사용하고 있습니다.
 **/
@Component
@Transactional(readOnly = true)
class MusinsaSoldoutCheckJob(
    val loadCrawlingProductsPort: LoadCrawlingProductsPort,
    val addBasketPort: AddBasketPort,
    val restClient: RestClient,
    val mailGateway: MailGateway,
    val sendSlackGateway: SendSlackGateway,
    val jsoupProductDataGateway: JsoupProductDataGateway,
    val fcmSendGateway: FcmSendGateway,
    val loadFcmTokenPort: LoadFcmTokenPort,
    val loadBasketPort: LoadBasketPort,
    val loadMemberPort: LoadMemberPort,
    val objectMapper: ObjectMapper,
    val addAlilmPort: AddAlilmPort
) : Job {

    private val log = LoggerFactory.getLogger(MusinsaSoldoutCheckJob::class.java)

    @Transactional
    override fun execute(context: JobExecutionContext) {
        val productList = loadCrawlingProductsPort.loadCrawlingProducts()

        productList.forEach { product ->
            val productNumber = product.number
            val requestUri = StringConstant.MUSINSA_API_URL_TEMPLATE.get().format(productNumber)
            val musinsaProductHtmlRequestUrl = StringConstant.MUSINSA_PRODUCT_HTML_REQUEST_URL.get().format(productNumber)

            val response = jsoupProductDataGateway.crawling(CrawlingGateway.CrawlingGatewayRequest(musinsaProductHtmlRequestUrl))
            val jsonData = extractJsonData(response.html, "window.__MSS__.product.state")
            val jsonObject = objectMapper.readTree(jsonData)

            // 상품의 전체 품절 여부
            val isGoodsSaleTypeEqualsSALE = jsonObject.get("goodsSaleType").toString() == "\"SALE\""

            val isSoldOut = if (isGoodsSaleTypeEqualsSALE.not()) {
                true
            } else {
                try {
                    checkIfSoldOut(requestUri, product)
                } catch (e: RestClientException) {
                    log.error("Failed to check soldout status of product: $productNumber", e)
                    sendSlackGateway.sendMessage("Failed to check soldout status of product number: $productNumber\nError: ${e.message}")
                    true // 상품이 품절로 간주
                }
            }

            if (!isSoldOut) {
                val baskets = loadBasketPort.loadBasket(product.id!!)

                baskets.forEach {
                    val member = loadMemberPort.loadMember(it.memberId.value) ?: throw NotFoundMemberException()
                    sendNotifications(product, member)

                    // 바구니 알림 상태로 변경
                    it.sendAlilm()
                    addBasketPort.addBasket(it, member, product)
                    addAlilmPort.addAlilm(Alilm.from(it))

                    val fcmTokenList = loadFcmTokenPort.loadFcmTokenAllByMember(it.memberId.value)

                    fcmTokenList.forEach { fcmToken ->
                        fcmSendGateway.sendFcmMessage(
                            member = member,
                            product = product,
                            fcmToken = fcmToken
                        )
                    }
                }
            }
        }
    }

    private fun sendNotifications(product: Product, member: Member) {
        mailGateway.sendMail(
            member.email,
            member.nickname,
            product.number,
            product.imageUrl,
            product.getEmailOption()
        )
        sendSlackGateway.sendMessage(getSlackMessage(product))
    }

    // 무신사 서버를 찔러요. ip 차단 시 서버를 재 시작 해야합니다.
    // 향후에는 비동기적으로 변경할 생각이에요.
    private fun checkIfSoldOut(requestUri: String, product: Product): Boolean {
        val response = restClient.get().uri(requestUri).retrieve().body<SoldoutCheckResponse>()
        val optionItem = response?.data?.optionItems?.firstOrNull {
            it.managedCode == product.getManagedCode() }

        return optionItem?.outOfStock ?: true
    }

    private fun getSlackMessage(product: Product): String {
        return """
            ${product.name} 상품이 재 입고 되었습니다.
            
            상품명: ${product.name}
            상품번호: ${product.number}
            상품 옵션1: ${product.firstOption}
            상품 옵션2: ${product.secondOption}
            상품 옵션3: ${product.thirdOption}
            상품 구매링크 : ${StringConstant.MUSINSA_PRODUCT_HTML_REQUEST_URL.get().format(product.number)}
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