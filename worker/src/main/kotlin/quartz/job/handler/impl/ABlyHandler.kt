package org.team_alilm.quartz.job.handler.impl

import org.slf4j.LoggerFactory
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import org.team_alilm.application.service.NotificationService
import org.team_alilm.domain.product.Product
import org.team_alilm.global.util.StringConstant
import org.team_alilm.quartz.job.handler.PlatformHandler

@Component
class ABlyHandler(
    private val restTemplate: RestTemplate,
    private val notificationService: NotificationService
) : PlatformHandler {

    private val log = LoggerFactory.getLogger(this::class.java)

    override fun process(product: Product) {
        if (!isSoldOut(product)) {
            notificationService.sendNotifications(product)
        }
    }

    private fun isSoldOut(product: Product): Boolean {
        val headers = HttpHeaders().apply {
            set("x-anonymous-token", StringConstant.ABLY_ANONYMOUS_TOKEN.get())
        }
        val entity = HttpEntity<Any>(headers)

        var selectedOptionSno: Long? = null

        for (depth in 1..3) {
            val url = buildApiUrl(product.number, depth, selectedOptionSno)
            val response = fetchOptionData(url, entity) ?: return true

            val matchingOption = response.option_components.firstOrNull {
                it.name == product.getOptionNameByDepth(depth)
            } ?: return true // 옵션이 없으면 품절로 간주

            if (matchingOption.is_final_depth) {
                return matchingOption.goods_option?.is_soldout ?: true
            }

            selectedOptionSno = matchingOption.goods_option_sno
        }

        return false
    }

    private fun buildApiUrl(productNumber: Long, depth: Int, selectedOptionSno: Long?): String {
        val baseUrl = StringConstant.ABLY_PRODUCT_OPTIONS_API_URL.get().format(productNumber, depth)
        return if (selectedOptionSno != null) "$baseUrl&selected_option_sno=$selectedOptionSno" else baseUrl
    }

    private fun fetchOptionData(url: String, entity: HttpEntity<Any>): Option? {
        return try {
            val response: ResponseEntity<Option> = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                Option::class.java
            )
            response.body
        } catch (e: Exception) {
            // 에러 로그 및 슬랙 알림 처리
            handleApiException(e, url)
            null
        }
    }

    private fun handleApiException(e: Exception, url: String) {
        // 에러 로그 출력 및 알림 서비스 호출
        log.error("Error fetching data from URL: $url, Error: ${e.message}")
    }

    private fun Product.getOptionNameByDepth(depth: Int): String? {
        return when (depth) {
            1 -> firstOption
            2 -> secondOption
            3 -> thirdOption
            else -> null
        }
    }

    data class Option(
        val option_components: List<OptionComponent>
    )

    data class OptionComponent(
        val name: String,
        val is_final_depth: Boolean,
        val goods_option_sno: Long,
        val goods_option: GoodsOption?
    )

    data class GoodsOption(
        val is_soldout: Boolean
    )
}
