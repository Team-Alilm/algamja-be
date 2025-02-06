package org.team_alilm.application.service

import com.fasterxml.jackson.databind.JsonNode
import domain.product.Store
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClient
import org.team_alilm.application.port.use_case.ProductCrawlingUseCase
import org.team_alilm.error.ErrorCode
import org.team_alilm.error.CustomException
import org.team_alilm.gateway.CrawlingGateway
import org.team_alilm.gateway.SendSlackGateway
import util.StringContextHolder

@Service
class CM29ProductCrawlingService(
    private val restClient: RestClient,
    private val crawlingGateway: CrawlingGateway,
    private val slackGateway: SendSlackGateway
) : ProductCrawlingUseCase {

    private val log = LoggerFactory.getLogger(this::class.java)

    override fun crawling(command: ProductCrawlingUseCase.ProductCrawlingCommand): ProductCrawlingUseCase.CrawlingResult {
        val productNumber = getProductNumber(command.url)
        val productDetailApiUrl = StringContextHolder.CM29_PRODUCT_DETAIL_API_URL.get().format(productNumber)

        val productDetailResponse = fetchProductDetails(productDetailApiUrl, productNumber)
        val productDetailData = productDetailResponse["data"] ?: throw IllegalArgumentException("Invalid response data")
        val productCategory = productDetailData["frontCategoryInfo"]?.get(0) ?: throw IllegalArgumentException("Category not found")

        return ProductCrawlingUseCase.CrawlingResult(
            number = productNumber,
            name = productDetailData["itemName"]?.asText() ?: throw CustomException(ErrorCode.CM29_PRODUCT_NOT_FOUND),
            brand = productDetailData["frontBrand"]?.get("brandNameKor")?.asText() ?: throw CustomException(ErrorCode.CM29_PRODUCT_NOT_FOUND),
            thumbnailUrl = buildImageUrl(productDetailData, 0),
            imageUrlList = extractImageUrls(productDetailData),
            firstCategory = productCategory["category1Name"]?.asText() ?: "Unknown",
            secondCategory = productCategory["category2Name"]?.asText() ?: "Unknown",
            price = productDetailData["consumerPrice"]?.asInt() ?: 0,
            store = Store.CM29,
            firstOptions = extractOptions(productDetailData),
            secondOptions = extractOptions(productDetailData, 1),
            thirdOptions = extractOptions(productDetailData, 2)
        )
    }

    private fun fetchProductDetails(apiUrl: String, productNumber: Long): JsonNode {
        return try {
            restClient.get().uri(apiUrl).retrieve().body(JsonNode::class.java)
                ?: throw IllegalArgumentException("Null response from API")
        } catch (e: Exception) {
            log.error("❌ 상품 크롤링 실패: API 요청 오류 (URL: $apiUrl, Error: ${e.message})")
            slackGateway.sendMessage("❌ 상품 크롤링 실패: API 요청 오류 (Product: $productNumber, Error: ${e.message})")
            throw CustomException(ErrorCode.CM29_PRODUCT_NOT_FOUND)
        }
    }

    private fun getProductNumber(url: String): Long {
        val html = crawlingGateway.htmlCrawling(CrawlingGateway.CrawlingGatewayRequest(url)).document.html()
        val productUrl = """<meta property="al:web:url" content="(https://product\.29cm\.co\.kr/catalog/\d+)">""".toRegex().find(html)?.groups?.get(1)?.value
            ?: throw CustomException(ErrorCode.CM29_PRODUCT_NOT_FOUND)
        return productUrl.substringAfterLast("/").toLong()
    }

    private fun buildImageUrl(data: JsonNode, index: Int): String {
        return "https://img.29cm.co.kr" + (data["itemImages"]?.get(index)?.get("imageUrl")?.asText() ?: "")
    }

    private fun extractImageUrls(data: JsonNode): List<String> {
        return data["itemImages"]?.drop(1)?.mapNotNull { it["imageUrl"]?.asText()?.let { url -> "https://img.29cm.co.kr$url" } } ?: emptyList()
    }

    private fun extractOptions(data: JsonNode, depth: Int = 0): List<String> {
        return generateSequence(data["optionItems"]?.get("list")) { it.get(0)?.get("list") }
            .elementAtOrNull(depth)?.mapNotNull { it["title"]?.asText() } ?: emptyList()
    }
}