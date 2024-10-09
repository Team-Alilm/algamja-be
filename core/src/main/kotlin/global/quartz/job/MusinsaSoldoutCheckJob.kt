package org.team_alilm.global.quartz.job

import com.google.gson.JsonParser
import org.quartz.Job
import org.quartz.JobExecutionContext
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.client.RestClient
import org.springframework.web.client.RestClientException
import org.springframework.web.client.body
import org.teamalilm.alilm.adapter.out.gateway.FcmSendGateway
import org.teamalilm.alilm.adapter.out.gateway.JsoupProductDataGateway
import org.teamalilm.alilm.adapter.out.gateway.MailGateway
import org.teamalilm.alilm.adapter.out.gateway.SlackGateway
import org.teamalilm.alilm.application.port.out.*
import org.teamalilm.alilm.application.port.out.gateway.CrawlingGateway
import org.teamalilm.alilm.common.util.StringConstant
import org.teamalilm.alilm.global.quartz.data.SoldoutCheckResponse
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
    val addPricePort: AddPricePort,
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
            log.info("Checking product: ${basketAndMemberAndProduct.product}")

            val productId = basketAndMemberAndProduct.product.number
            val requestUri = StringConstant.MUSINSA_API_URL_TEMPLATE.get().format(productId)
            val musinsaProductHtmlRequestUrl = StringConstant.MUSINSA_PRODUCT_HTML_REQUEST_URL.get().format(productId)

            // 상품의 전체 품절 시 확인 하는 로직을 가지고 있어요.
            val document = jsoupProductDataGateway.crawling(CrawlingGateway.CrawlingGatewayRequest(musinsaProductHtmlRequestUrl)).document
            val scriptContent = document.getElementsByTag("script").html()
            val jsonData = extractJsonData(scriptContent, "window.__MSS__.product.state")
            val jsonObject = JsonParser.parseString(jsonData).asJsonObject

            val isAllSoldout = jsonObject.get("goodsSaleType").asString == "SOLDOUT"
            val price = jsonObject.get("goodsPrice").asJsonObject.get("salePrice").asInt

            // salePrice 기준으로 가격 히스토리를 저장해요. (임시 주석처리)
//            addPricePort.addPrice(
//                price,
//                basketAndMemberAndProduct.product
//            )

            // isAllSoldout이 true일 경우 API 호출 생략
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

                val fcmTokenList = loadFcmTokenPort.loadFcmTokenAllByMember(basketAndMemberAndProduct.member)

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
        mailGateway.sendMail(getEmailMessage(basketAndMemberAndProduct), basketAndMemberAndProduct.member.email)
        slackGateway.sendMessage(getSlackMessage(basketAndMemberAndProduct))
    }

    private fun checkIfSoldOut(requestUri: String, basketAndMemberAndProduct: LoadAllBasketsPort.BasketAndMemberAndProduct): Boolean {
        val response = restClient.get().uri(requestUri).retrieve().body<SoldoutCheckResponse>()
        val optionItem = response?.data?.optionItems?.firstOrNull {
            it.managedCode == basketAndMemberAndProduct.getManagedCode() }

        return optionItem?.outOfStock ?: true
    }

    private fun getEmailMessage(basketAndMemberAndProduct: LoadAllBasketsPort.BasketAndMemberAndProduct): String {
        return """
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>재입고 알림</title>
            </head>
            <body style="font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 0;">
            <table style="width: 100%; max-width: 600px; margin: 20px auto; background-color: #ffffff; border-radius: 8px; box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);">
                <tr>
                    <td style="background-color: #1B1A3B; color: #ffffff; text-align: center; padding: 20px;">
                        <h1 style="margin: 0;">Alilm</h1>
                    </td>
                </tr>
                <tr>
                    <td style="padding: 20px;">
                        <h2>${basketAndMemberAndProduct.member.nickname}님이 등록하신 제품이 재입고 되었습니다!</h2>
                        <table style="width: 100%; margin-bottom: 20px;">
                            <tr>
                                <td style="width: 68px;">
                                    <img src="${basketAndMemberAndProduct.product.imageUrl}" alt="Product Image" style="border-radius: 4px; width: 68px; height: 80px;">
                                </td>
                                <td style="padding-left: 20px;">
                                    <p style="margin: 0;"><strong>상품 옵션:</strong> ${basketAndMemberAndProduct.product.name}</p>
                                    <p style="margin: 0;"><strong>재입고 시각:</strong> ${LocalDateTime.now().atZone(ZoneId.of("Asia/Seoul")).toLocalDateTime()}</p>
                                </td>
                            </tr>
                        </table>
                        <p>${basketAndMemberAndProduct.member.nickname}님이 등록하신 상품의 재입고 소식을 알려드리러 왔어요.</p>
                        <p>상품은 재입고 시각으로부터 다시 품절이 될 수 있음을 유의해주세요!</p>
                        <p>저희 알림 서비스를 이용해주셔서 감사합니다 :)</p>
                        <a href="https://www.musinsa.com/app/goods/${basketAndMemberAndProduct.product.number}" style="display: block; width: 100%; max-width: 560px; margin: 20px auto; padding: 15px; background-color: #1B1A3B; color: #ffffff; text-align: center; text-decoration: none; border-radius: 4px; font-size: 18px; font-weight: bold;">
                            재입고 상품 구매하러 가기 👉
                        </a>
                    </td>
                </tr>
                <tr>
                    <td style="background-color: #f4f4f4; text-align: center; padding: 10px; font-size: 14px; color: #555555;">
                        알림 서비스 © 2024 Alilm
                    </td>
                </tr>
            </table>
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