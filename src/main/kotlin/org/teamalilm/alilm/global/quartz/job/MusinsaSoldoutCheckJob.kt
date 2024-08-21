package org.teamalilm.alilm.global.quartz.job

import org.quartz.Job
import org.quartz.JobExecutionContext
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.client.RestClient
import org.springframework.web.client.RestClientException
import org.springframework.web.client.body
import org.teamalilm.alilm.adapter.out.gateway.MailGateway
import org.teamalilm.alilm.adapter.out.gateway.SlackGateway
import org.teamalilm.alilm.application.port.out.AddBasketPort
import org.teamalilm.alilm.application.port.out.LoadAllBasketsPort
import org.teamalilm.alilm.application.port.out.SendAlilmBasketPort
import org.teamalilm.alilm.common.companion.StringConstant
import org.teamalilm.alilm.common.error.MusinsaSoldoutCheckException
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
    val restClient: RestClient,
    val mailGateway: MailGateway,
    val slackGateway: SlackGateway,
    val sendAlilmBasketPort: SendAlilmBasketPort
) : Job {

    private val log = LoggerFactory.getLogger(MusinsaSoldoutCheckJob::class.java)

    @Transactional
    override fun execute(context: JobExecutionContext) {
        val basketAndMemberAndProducts = loadAllBasketsPort.getAllBaskets()

        basketAndMemberAndProducts.forEach { basketAndMemberAndProduct ->
            log.info("Checking product: ${basketAndMemberAndProduct.product}")

            val productId = basketAndMemberAndProduct.product.number
            val requestUri = StringConstant.MUSINSA_API_URL_TEMPLATE.get().format(productId)

            val isSoldOut = try {
                checkIfSoldOut(requestUri, basketAndMemberAndProduct)
            } catch (e: RestClientException) {
                log.info("Failed to check soldout status of product: $productId")
                true
            }

            if (!isSoldOut) {
                sendNotifications(basketAndMemberAndProduct)
                basketAndMemberAndProduct.basket.sendAlilm()

                addBasketPort.addBasket(
                    basketAndMemberAndProduct.basket,
                    basketAndMemberAndProduct.member,
                    basketAndMemberAndProduct.product
                )

                sendAlilmBasketPort.sendAlilmBasket(
                    basketAndMemberAndProduct.basket,
                    basketAndMemberAndProduct.member,
                    basketAndMemberAndProduct.product
                )
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
            log.info("ManagedCode: ${it.managedCode}")
            log.info("BasketAndMemberAndProduct: ${basketAndMemberAndProduct.getManagedCode()}")

            it.managedCode == basketAndMemberAndProduct.getManagedCode()
        }
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
                <style>
                    body {
                        font-family: Arial, sans-serif;
                        background-color: #f4f4f4;
                        margin: 0;
                        padding: 0;
                    }
                    .container {
                        width: 100%;
                        max-width: 600px;
                        margin: 20px auto;
                        background-color: #ffffff;
                        border-radius: 8px;
                        box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
                        overflow: hidden;
                    }
                    .header {
                        background-color: #1B1A3B;
                        color: #ffffff;
                        text-align: center;
                        padding: 20px;
                    }
                    .header h1 {
                        margin: 0;
                    }
                    .content {
                        padding: 20px;
                    }
                    .product-info {
                        display: flex;
                        gap: 20px;
                        margin-bottom: 20px;
                    }
                    .product-info img {
                        border-radius: 4px;
                        width: 68px;
                        height: 80px;
                    }
                    .product-info div {
                        flex: 1;
                    }
                    .button {
                        display: block;
                        width: 100%;
                        max-width: 560px;
                        margin: 20px auto;
                        padding: 15px;
                        background-color: #1B1A3B;
                        color: #ffffff;
                        text-align: center;
                        text-decoration: none;
                        border-radius: 4px;
                        font-size: 18px;
                        font-weight: bold;
                    }
                    .footer {
                        background-color: #f4f4f4;
                        text-align: center;
                        padding: 10px;
                        font-size: 14px;
                        color: #555555;
                    }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>Alilm</h1>
                    </div>
                    <div class="content">
                        <h2>${basketAndMemberAndProduct.member.nickname}님이 등록하신 제품이 재입고 되었습니다!</h2>
                        <div class="product-info">
                            <img src="${basketAndMemberAndProduct.product.imageUrl}" alt="Product Image"/>
                            <div>
                                <p><strong>상품 옵션:</strong> ${basketAndMemberAndProduct.product.name}</p>
                                <p><strong>재입고 시각:</strong> ${LocalDateTime.now().atZone(ZoneId.of("Asia/Seoul")).toLocalDateTime()}</p>
                            </div>
                        </div>
                        <p>${basketAndMemberAndProduct.member.nickname}님이 등록하신 상품의 재입고 소식을 알려드리러 왔어요.</p>
                        <p>상품은 재입고 시각으로부터 다시 품절이 될 수 있음을 유의해주세요!</p>
                        <p>저희 알림 서비스를 이용해주셔서 감사합니다 :)</p>
                        <a href="https://www.musinsa.com/app/goods/${basketAndMemberAndProduct.product.number}" class="button">
                            재입고 상품 구매하러 가기 👉
                        </a>
                    </div>
                    <div class="footer">
                        알림 서비스 © 2024 Alilm
                    </div>
                </div>
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

}