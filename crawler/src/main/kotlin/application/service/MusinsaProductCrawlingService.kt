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
import org.team_alilm.gateway.CrawlingGateway.CrawlingGatewayRequest
import util.StringContextHolder

@Service
class MusinsaProductCrawlingService(
    private val crawlingGateway: CrawlingGateway,
    private val restClient: RestClient
) : ProductCrawlingUseCase {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun crawling(command: ProductCrawlingUseCase.ProductCrawlingCommand): ProductCrawlingUseCase.CrawlingResult {
        val crawlingGatewayResponse = crawlingGateway.htmlCrawling(request = CrawlingGatewayRequest(url = command.url))

        val productHtmlResponse = extractJsonData(crawlingGatewayResponse.document.html())

        val optionUri = getOptionUri(productHtmlResponse.get("goodsNo").asLong())
        val optionResponse = restClient.get()
            .uri(optionUri)
            .retrieve()
            .body(JsonNode::class.java)
            ?: throw CustomException(ErrorCode.MUSINSA_INVALID_RESPONSE)

        log.info("optionResponse: $optionResponse")

        val filterOption = optionResponse.get("data")?.get("basic")
            ?: throw CustomException(ErrorCode.MUSINSA_PRODUCT_NOT_FOUND)

        val firstOptions = filterOption[0]?.get("optionValues")?.map { it.get("name").asText() } ?: throw CustomException(ErrorCode.MUSINSA_PRODUCT_NOT_FOUND)
        val secondOptions = filterOption[1]?.get("optionValues")?.map { it.get("name").asText() } ?: emptyList()
        val thirdOptions = filterOption[2]?.get("optionValues")?.map { it.get("name").asText() } ?: emptyList()

        val imageUrlListRequestUri = StringContextHolder.MUSINSA_PRODUCT_IMAGES_URL.get().format(productHtmlResponse.get("goodsNo").asLong())
        val imageUrlListResponse = restClient.get()
            .uri(imageUrlListRequestUri)
            .retrieve()
            .body(JsonNode::class.java)
            ?: throw CustomException(ErrorCode.MUSINSA_API_ERROR)

        return ProductCrawlingUseCase.CrawlingResult(
            number = productHtmlResponse.get("goodsNo").asLong(),
            name = productHtmlResponse.get("goodsNm").asText(),
            brand = productHtmlResponse.get("brandInfo").get("brandName").asText(),
            thumbnailUrl = getThumbnailUrl(productHtmlResponse.get("thumbnailImageUrl").asText()),
            imageUrlList = imageUrlListResponse.get("data")?.get("similar")?.get(0)?.get("recommendedGoodsList")?.map { it.get("imageUrl").asText() }
                ?: emptyList(),
            firstCategory = productHtmlResponse.get("category").get("categoryDepth1Name").asText(),
            secondCategory = productHtmlResponse.get("category").get("categoryDepth2Name").asText(),
            price = productHtmlResponse.get("goodsPrice").get("normalPrice").asInt(),
            store = Store.MUSINSA,
            firstOptions = firstOptions,
            secondOptions = secondOptions,
            thirdOptions = thirdOptions
        )
    }


    private fun extractJsonData(scriptContent: String): JsonNode {
        var jsonString: String? = null

        val pattern = "window.__MSS__.product.state = "
        val startIndex = scriptContent.indexOf(pattern)

        if (startIndex != -1) {
            val substring = scriptContent.substring(startIndex + pattern.length)
            val endIndex = substring.indexOf("};") + 1
            jsonString = substring.substring(0, endIndex)
        }

        return jsonString?.let {
            val mapper = com.fasterxml.jackson.databind.ObjectMapper()
            mapper.readTree(it)
        } ?: throw CustomException(ErrorCode.MUSINSA_URL_PARSING_ERROR)
    }

    private fun getOptionUri(goodsNo: Long): String {
        return "https://goods-detail.musinsa.com/api2/goods/$goodsNo/v2/options?goodsSaleType=SALE"
    }

    private fun getThumbnailUrl(thumbnailUrl: String): String {
        return "https://image.msscdn.net/thumbnails${thumbnailUrl}"
    }
}
