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
        }
    }

    private fun checkSoldOut(product: Product): Boolean {
        val ablyAnonymousToken = StringConstant.ABLY_ANONYMOUS_TOKEN
        val ablyProductOptions = restTemplate.getForObject(
            StringConstant.ABLY_PRODUCT_OPTIONS_API_URL.get().format(product.number),
            Option::class.java
        )

        return false
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