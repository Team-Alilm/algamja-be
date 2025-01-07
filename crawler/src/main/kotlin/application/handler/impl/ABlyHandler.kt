package org.team_alilm.application.handler.impl

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import domain.product.Product
import org.team_alilm.application.handler.PlatformHandler
import util.StringContextHolder

@Component
class ABlyHandler(
    private val restClient: RestClient,
) : PlatformHandler {

    private val log = LoggerFactory.getLogger(this::class.java)

    override fun process(product: Product): Boolean {
        return !isSoldOut(product)
    }

    private fun isSoldOut(product: Product): Boolean {
        return false
    }

    private fun buildApiUri(productNumber: Long, depth: Int, selectedOptionSno: Long?): String {
        val baseUrl = StringContextHolder.ABLY_PRODUCT_OPTIONS_API_URL.get().format(productNumber, depth)
        return if (selectedOptionSno != null) "$baseUrl&selected_option_sno=$selectedOptionSno" else baseUrl
    }

    private fun fetchOptionData(uri: String): Option? {
        return try {
            restClient.get()
                .uri(uri)
                .header("x-anonymous-token", )
                .retrieve()
                .body(Option::class.java)

        } catch (e: Exception) {
            // 에러 로그 및 슬랙 알림 처리
            handleApiException(e, uri)
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
