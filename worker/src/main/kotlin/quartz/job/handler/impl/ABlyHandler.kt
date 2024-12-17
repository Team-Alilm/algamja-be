package org.team_alilm.quartz.job.handler.impl

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClientException
import org.springframework.web.client.RestTemplate
import org.team_alilm.adapter.out.gateway.FcmSendGateway
import org.team_alilm.adapter.out.gateway.MailGateway
import org.team_alilm.application.port.out.gateway.crawling.CrawlingGateway
import org.team_alilm.domain.product.Product
import org.team_alilm.global.util.StringConstant
import org.team_alilm.quartz.job.handler.PlatformHandler


@Component
class ABlyHandler(
    private val sendSlackGateway: CrawlingGateway,
    private val restTemplate: RestTemplate,
    private val mailGateway: MailGateway,
    private val fcmSendGateway: FcmSendGateway
) : PlatformHandler {

    override fun process(product: Product) {
        if(checkSoldOut(product).not()) {
            sendNotifications(product)
        }
    }

    private fun checkSoldOut(product: Product): Boolean {
        val ablyAnonymousToken = StringConstant.ABLY_ANONYMOUS_TOKEN
        val ablyProductOptions = restTemplate.getForObject(
            StringConstant.ABLY_PRODUCT_OPTIONS_API_URL.get().format(product.number),
            Option::class.java
        )



        return if (jsonData != null) {
            // JSON 데이터 파싱
            val jsonObject = objectMapper.readTree(jsonData)
            val isGoodsSaleTypeEqualsSALE = jsonObject.get("goodsSaleType").toString() == "\"SALE\""

            if (isGoodsSaleTypeEqualsSALE.not()) {
                true
            } else {
                // API 호출로 재확인
                val requestUri = StringConstant.MUSINSA_OPTION_API_URL.get().format(product.number)
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
            val requestUri = StringConstant.MUSINSA_OPTION_API_URL.get().format(product.number)

            try {
                checkIfSoldOut(requestUri, product)
            } catch (e: RestClientException) {
                log.error("Failed to check soldout status of product: ${product.number}", e)
                sendSlackGateway.sendMessage("무신사 서버에 요청 시 에러가 발생했어요.: ${product.number}\nError: ${e.message}")
                true
            }
        }
    }

    data class Option(
        val sno: Long,
        val depth: Int,
        val name: String,
        val is_final_depth: Boolean,
        val goods_option_sno: Long,
        val delivery_type: String,
        val standard_delivery_message: String,
        val delivery_lead_days: Any?, // null 가능
        val goods_option: Any?, // null 가능
        val wholesale_name: Any?, // null 가능
        val has_high_demand_tag: Boolean
    )

    data class Color(
        val name: String,
        val option_components: List<Option>
    )

}