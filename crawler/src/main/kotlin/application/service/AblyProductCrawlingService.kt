package org.team_alilm.application.service

import com.fasterxml.jackson.databind.JsonNode
import com.google.gson.JsonObject
import domain.product.Store
import org.openqa.selenium.By
import org.openqa.selenium.JavascriptExecutor
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClient
import org.team_alilm.application.port.use_case.ProductCrawlingUseCase
import org.team_alilm.error.AnonymousTokenException
import util.StringContextHolder
import java.net.URI

@Service
class AblyProductCrawlingService(
    private val restClient: RestClient,
    private val webDriver: org.openqa.selenium.WebDriver
) : ProductCrawlingUseCase {

    private val log = org.slf4j.LoggerFactory.getLogger(javaClass)

    override fun crawling(command: ProductCrawlingUseCase.ProductCrawlingCommand): ProductCrawlingUseCase.CrawlingResult {
        webDriver.get(command.url)
        val productNumber = getProductNumber(command.url)

        // JSON 파싱하여 `ably-anonymous-token: ${token}` 추출
        val pageSource = webDriver.pageSource
        log.info("Page source: $pageSource")

// "ably-anonymous-token: " 문자열 이후부터 시작하여, 해당 토큰 값을 추출
        val tokenStartIndex = pageSource.indexOf("{\"ably-anonymous-token\":\"") + "{\"ably-anonymous-token\":\"".length

        log.info("Ably anonymous token start index: $tokenStartIndex")
// 토큰의 끝 부분을 찾기 위한 인덱스 (공백, 괄호, 따옴표 등으로 끝날 수 있음)
        val tokenEndIndex = pageSource.indexOf("\"}", tokenStartIndex)
        log.info("Ably anonymous token end index: $tokenEndIndex")
        val aNonymousToken = pageSource.substring(tokenStartIndex, tokenEndIndex)

        log.info("Ably anonymous token: $aNonymousToken")
//        val aNonymousToken = restClient.get()
//            .uri(StringContextHolder.ABLY_ANONYMOUS_TOKEN_API_URL.get())
//            .accept(MediaType.APPLICATION_JSON)
//            .retrieve()
//            .body(JsonNode::class.java)
//            ?.get("token")
//            ?.asText() ?: throw AnonymousTokenException()

        // https://m.a-bly.com/goods/34883322
        val productDetails = getProductDetails(
            productNumber = productNumber,
            aNonymousToken = aNonymousToken
        )

        val firstOptions = getProductOptions(
            productNumber = productNumber,
            optionDepth = 1, selectedOptionSno = null,
            aNonymousToken = aNonymousToken
        ) ?: throw IllegalArgumentException()
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

    private fun getProductDetails(
        productNumber: Long,
        aNonymousToken: String
    ): JsonNode? {
        try {
            return restClient.get()
                .uri(StringContextHolder.ABLY_PRODUCT_API_URL.get().format(productNumber))
                .accept(MediaType.APPLICATION_JSON)
                .header("X-Anonymous-Token", aNonymousToken)
                .retrieve()
                .body(JsonNode::class.java)


        } catch (e: Exception) {
            log.error("Error while fetching product details: ${e.message}")
            return null
        }
    }

    private fun getProductOptions(
        productNumber: Long,
        optionDepth: Int,
        selectedOptionSno: Long?,
        aNonymousToken: String
    ): JsonNode? {
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
