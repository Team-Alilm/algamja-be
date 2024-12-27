package org.team_alilm.application.service

import com.fasterxml.jackson.databind.JsonNode
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClient
import org.team_alilm.application.port.`in`.use_case.product.crawling.ProductCrawlingUseCase
import org.team_alilm.domain.product.Store
import org.team_alilm.global.error.NotFoundProductException
import org.team_alilm.global.util.StringContextHolder
import java.net.URI

@Service
class AblyProductCrawlingService(
    private val restClient: RestClient,
) : ProductCrawlingUseCase {

    private val log = org.slf4j.LoggerFactory.getLogger(javaClass)

    override fun crawling(command: ProductCrawlingUseCase.ProductCrawlingCommand): ProductCrawlingUseCase.CrawlingResult {
        val productNumber = getProductNumber(command.url)
        val aNonymousToken = restClient.get()
            .uri(StringContextHolder.ABLY_ANONYMOUS_TOKEN_API_URL.get())
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .body(JsonNode::class.java)
            ?.get("token")
            ?.asText() ?: throw IllegalArgumentException("익명 토큰을 가져올 수 없습니다.")

        // https://m.a-bly.com/goods/34883322
        val productDetails = getProductDetails(productNumber = productNumber, aNonymousToken = aNonymousToken)
        log.info("productDetails: $productDetails")

        val firstOptions = getProductOptions(
            productNumber = productNumber,
            optionDepth = 1, selectedOptionSno = null,
            aNonymousToken = aNonymousToken
        ) ?: throw NotFoundProductException()
        val secondOptions = getProductOptions(
            productNumber = productNumber,
            optionDepth = 2,
            selectedOptionSno = firstOptions.get("option_components")?.first()?.get("goods_option_sno")?.asLong(),
            aNonymousToken = aNonymousToken
        )
        val thirdOptions = getProductOptions(
            productNumber = productNumber,
            optionDepth = 3,
            selectedOptionSno = secondOptions?.get("option_components")?.first()?.get("goods_option_sno")?.asLong(),
            aNonymousToken = aNonymousToken
        )

        return ProductCrawlingUseCase.CrawlingResult(
            number = productDetails?.get("goods")?.get("sno")?.asLong() ?: throw IllegalArgumentException("상품 정보를 가져올 수 없습니다."),
            name = productDetails.get("goods")?.get("name")?.asText() ?: throw IllegalArgumentException("상품 정보를 가져올 수 없습니다."),
            brand = productDetails.get("goods")?.get("market")?.get("name")?.asText() ?: throw IllegalArgumentException("상품 정보를 가져올 수 없습니다."),
            thumbnailUrl = productDetails.get("goods")?.get("first_page_rendering")?.get("cover_image")?.asText() ?: throw IllegalArgumentException("상품 정보를 가져올 수 없습니다."),
            imageUrlList = productDetails.get("goods")?.get("cover_images")?.map { it.asText() } ?: emptyList(),
            firstCategory = productDetails.get("goods")?.get("category")?.get("name")?.asText() ?: throw IllegalArgumentException("상품 정보를 가져올 수 없습니다."),
            secondCategory = null,
            price = productDetails.get("goods")?.get("first_page_rendering")?.get("original_price")?.asInt() ?: throw IllegalArgumentException("상품 정보를 가져올 수 없습니다."),
            store = Store.A_BLY,
            firstOptions = firstOptions.get("option_components")?.map { it.get("name")?.asText() ?: "" } ?: emptyList(),
            secondOptions = secondOptions?.get("option_components")?.map { it.get("name")?.asText() ?: "" } ?: emptyList(),
            thirdOptions = thirdOptions?.get("option_components")?.map { it.get("name")?.asText() ?: "" } ?: emptyList(),
        )
    }

    private fun getProductNumber(url: String): Long {
        return url.split("/").last().toLong()
    }

    private fun getProductDetails(productNumber: Long, aNonymousToken: String): JsonNode? {
        try {
            restClient.get()
                .uri(StringContextHolder.ABLY_PRODUCT_API_URL.get().format(productNumber))
                .accept(MediaType.APPLICATION_JSON)
                .header("X-Anonymous-Token", aNonymousToken)
                .exchange{
                    request, response ->
                    log.info("Request uri: ${request.uri}")
                    log.info("Request method: ${request.method}")
                    log.info("Request headers: ${request.headers}")
                    log.info("Response body: ${response.body}")
                    log.info("Response headers: ${response.headers}")
                    log.info("Response status: ${response.statusCode}")
                }

            return null
        } catch (e: Exception) {
            log.error("Error while fetching product details: ${e.message}")
            return null
        }
    }

    private fun getProductOptions(productNumber: Long, optionDepth: Int, selectedOptionSno: Long?, aNonymousToken: String): JsonNode? {
        return try {
            restClient.get()
                .uri {
                    val uri = StringContextHolder.ABLY_PRODUCT_OPTIONS_API_URL.get().format(productNumber, optionDepth)
                    val selectedOptionParam = selectedOptionSno?.let { "&selected_option_sno=$it" } ?: ""
                    URI(uri + selectedOptionParam)
                }
                .accept(MediaType.APPLICATION_JSON)
                .header("X-Anonymous-Token", aNonymousToken)
                .retrieve()
                .body(JsonNode::class.java)
        } catch (e: Exception) {
            log.info("Error while fetching product options: ${e.message}")
            return null
        }
    }
}
