package org.team_alilm.quartz.job

import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.*
import org.quartz.Job
import org.quartz.JobExecutionContext
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.client.RestClient
import org.springframework.web.client.RestClientException
import org.springframework.web.client.body
import org.team_alilm.adapter.out.gateway.FcmSendGateway
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
    private val loadCrawlingProductsPort: LoadCrawlingProductsPort,
    private val addBasketPort: AddBasketPort,
    private val restClient: RestClient,
    private val mailGateway: MailGateway,
    private val sendSlackGateway: SendSlackGateway,
    private val crawlingGateway: CrawlingGateway,
    private val fcmSendGateway: FcmSendGateway,
    private val loadFcmTokenPort: LoadFcmTokenPort,
    private val loadBasketPort: LoadBasketPort,
    private val loadMemberPort: LoadMemberPort,
    private val objectMapper: ObjectMapper,
    private val addAlilmPort: AddAlilmPort,
    private val coroutineScope: CoroutineScope
) : Job {

    private val log = LoggerFactory.getLogger(MusinsaSoldoutCheckJob::class.java)

    @Transactional
    override fun execute(context: JobExecutionContext) {
        val productList = loadCrawlingProductsPort.loadCrawlingProducts()
        log.info("Checking soldout status for ${productList.size} products.")

        // 비동기 작업으로 전환해요.
        coroutineScope.launch {
            val soldoutCheckResults = productList.map { product ->
                async(Dispatchers.IO) {
                    checkIfSoldOutForProduct(product)
                }
            }.awaitAll()

            // 품절 상태를 체크한 후 알림 처리
            val handleJobs = soldoutCheckResults.mapIndexedNotNull { index, isSoldOut ->
                log.info("""
                    ${productList[index].number}  
                    ${productList[index].name}
                    ${productList[index].firstOption}
                    soldout status: $isSoldOut
                """.trimIndent())
                val product = productList[index]
                if (!isSoldOut) {
                    async(Dispatchers.IO) {
                        handleAvailableProduct(product)
                    }
                } else {
                    log.info("Product ${product.number} is sold out.")
                    null
                }
            }

            handleJobs.awaitAll() // 여기서 awaitAll() 사용
        }
    }

    private suspend fun handleAvailableProduct(product: Product) {
        val baskets = loadBasketPort.loadBasketIncludeIsDelete(product.id!!)
        baskets.forEach { basket ->
            val member = loadMemberPort.loadMember(basket.memberId.value) ?: throw NotFoundMemberException()
            sendNotifications(product, member)

            // 바구니 알림 상태로 변경
            basket.sendAlilm()
            addBasketPort.addBasket(basket, member, product)
            addAlilmPort.addAlilm(Alilm.from(basket))

            val fcmTokenList = loadFcmTokenPort.loadFcmTokenAllByMember(basket.memberId.value)

            fcmTokenList.forEach { fcmToken ->
                fcmSendGateway.sendFcmMessage(
                    member = member,
                    product = product,
                    fcmToken = fcmToken
                )
            }
        }
    }


    private suspend fun checkIfSoldOutForProduct(product: Product): Boolean {
        val musinsaProductHtmlRequestUrl = StringConstant.MUSINSA_PRODUCT_HTML_REQUEST_URL.get().format(product.number)

        // HTML 크롤링을 통한 품절 확인
        val response = crawlingGateway.crawling(CrawlingGateway.CrawlingGatewayRequest(musinsaProductHtmlRequestUrl))
        val jsonData = extractJsonData(response.html, "window.__MSS__.product.state")

        return if (jsonData != null) {
            val jsonObject = objectMapper.readTree(jsonData)
            val isGoodsSaleTypeEqualsSALE = jsonObject.get("goodsSaleType").toString() == "\"SALE\""

            if (isGoodsSaleTypeEqualsSALE.not()) {
                true // SALE이 아니면 품절
            } else {
                // API 호출로 재확인
                val requestUri = StringConstant.MUSINSA_API_URL_TEMPLATE.get().format(product.number)
                try {
                    checkIfSoldOut(requestUri, product)
                } catch (e: RestClientException) {
                    log.error("Failed to check soldout status of product: ${product.number}", e)
                    sendSlackGateway.sendMessage("Failed to check soldout status of product number: ${product.number}\nError: ${e.message}")
                    true // 상품이 품절로 간주
                }
            }
        } else {
            // JSON 데이터가 없을 경우 API 호출로 재확인
            log.error("No JSON data found for product: ${product.number}")
            val requestUri = StringConstant.MUSINSA_API_URL_TEMPLATE.get().format(product.number)
            checkIfSoldOut(requestUri, product)
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