package org.team_alilm.application.service

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
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
        val aNonymousToken = restClient.get()
            .uri(StringContextHolder.ABLY_ANONYMOUS_TOKEN_API_URL.get())
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .body(JsonNode::class.java)
            ?.get("token")
            ?.asText() ?: throw IllegalArgumentException("익명 토큰을 가져올 수 없습니다.")

        // https://m.a-bly.com/goods/34883322
        val productDetails = getProductDetails(url = command.url, productNumber = command.productNumber, aNonymousToken = aNonymousToken)

        log.info("productDetails: $productDetails")

        val firstOptions = getProductOptions(
            productNumber = command.productNumber,
            optionDepth = 1, selectedOptionSno = null,
            aNonymousToken = aNonymousToken
        ) ?: throw NotFoundProductException()
        val secondOptions = getProductOptions(
            productNumber = command.productNumber,
            optionDepth = 2,
            selectedOptionSno = firstOptions.get("option_components")?.first()?.get("goods_option_sno")?.asLong(),
            aNonymousToken = aNonymousToken
        )
        val thirdOptions = getProductOptions(
            productNumber = command.productNumber,
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

    private fun getProductDetails(url: String, productNumber: Long, aNonymousToken: String): JsonNode? {
        val options = ChromeOptions()
        options.addArguments("--headless")
        options.addArguments("--no-sandbox")
        options.addArguments("--disable-dev-shm-usage")
        options.addArguments("--disable-gpu")

        // ChromeDriver 초기화
        val driver = ChromeDriver(options)
        val objectMapper = ObjectMapper()

        return try {
            driver.get(url)

            val response = (driver as JavascriptExecutor).executeScript("""
                return fetch("${StringContextHolder.ABLY_PRODUCT_API_URL.get().format(productNumber)}", {
                    method: "GET",
                    headers: {
                        "X-Anonymous-Token": "$aNonymousToken"
                    }
                }).then(response => response.json());
            """.trimIndent()).toString()

            log.info("response: $response")

            response.let {
                objectMapper.readTree(it)
            }
        } catch (e: Exception) {
            log.info("Error while fetching product details: ${e.message}")
            null
        } finally {
            driver.quit()
        }
    }

    private fun getProductOptions(productNumber: Long, optionDepth: Int, selectedOptionSno: Long?, aNonymousToken: String): JsonNode? {
        log.info("productNumber: $productNumber, optionDepth: $optionDepth, selectedOptionSno: $selectedOptionSno")
        log.info("url: ${StringContextHolder.ABLY_PRODUCT_OPTIONS_API_URL.get().format(productNumber, optionDepth)}")
        try {
            restClient.get()
                .uri {
                    val uri = StringContextHolder.ABLY_PRODUCT_OPTIONS_API_URL.get().format(productNumber, optionDepth)
                    val selectedOptionParam = selectedOptionSno?.let { "&selected_option_sno=$it" } ?: ""
                    URI(uri + selectedOptionParam)
                }
                .accept(MediaType.APPLICATION_JSON)
                .header("X-Anonymous-Token", aNonymousToken)
                .exchange { requset, response ->
                    log.info("request: ${requset.uri}")
                    log.info("request: ${requset.headers}")
                    log.info("response: ${response.headers}")
                    log.info("response: ${response.statusCode}")
                    log.info("response: ${response.body}")
                    return@exchange response.body
                }


            return null
        } catch (e: Exception) {
            log.info("Error while fetching product options: ${e.message}")
            return null
        }
    }
}