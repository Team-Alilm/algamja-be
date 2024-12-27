package org.team_alilm.application.service

import com.fasterxml.jackson.databind.JsonNode
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.client.RestTemplate
import org.team_alilm.application.port.`in`.use_case.product.crawling.ProductCrawlingUseCase
import org.team_alilm.application.port.out.gateway.crawling.CrawlingGateway
import org.team_alilm.application.port.out.gateway.crawling.CrawlingGateway.*
import org.team_alilm.domain.product.Store
import org.team_alilm.global.util.StringContextHolder

@Service
@Transactional(readOnly = true)
class MusinsaProductCrawlingService(
    private val crawlingGateway: CrawlingGateway,
    private val restTemplate: RestTemplate
) : ProductCrawlingUseCase {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun crawling(command: ProductCrawlingUseCase.ProductCrawlingCommand): ProductCrawlingUseCase.CrawlingResult {
        val crawlingGatewayRequest = CrawlingGatewayRequest(command.url)
        val crawlingGatewayResponse = crawlingGateway.htmlCrawling(crawlingGatewayRequest)

        val jsonData = extractJsonData(crawlingGatewayResponse.html)
            ?: throw RuntimeException("Failed to extract JSON data from script content")

        val crawlingRequest = try {
            Gson().fromJson(jsonData, CrawlingGatewayRequest::class.java)
        } catch (e: Exception) {
            log.error("Invalid JSON data: $jsonData", e)
            throw RuntimeException("Invalid JSON data", e)
        }

        val optionUrl = getOptionUrl(crawlingRequest.goodsNo)
        log.info("Option URL: $optionUrl")
        val optionResponse = restTemplate.getForEntity(optionUrl, JsonNode::class.java).body
        val filterOption = optionResponse?.get("data")?.get("filterOption") ?: throw RuntimeException("Failed to get option data")

        val firstOptions = filterOption.get("firstOptions").map { it.get("val").asText() }
        val secondOptions = filterOption.get("secondOptions")?.map { it.get("val").asText() } ?: emptyList()
        val thirdOptions = filterOption.get("thirdOptions")?.map { it.get("val").asText() } ?: emptyList()

        val imageUrlListRequsetUrl = StringContextHolder.MUSINSA_PRODUCT_IMAGES_URL.get().format(crawlingRequest.goodsNo)
        val imageUrlListResponse = restTemplate.getForEntity(imageUrlListRequsetUrl, JsonNode::class.java).body

        return ProductCrawlingUseCase.CrawlingResult(
            number = crawlingRequest.goodsNo,
            name = crawlingRequest.goodsNm,
            brand = crawlingRequest.brandInfo.brandName,
            thumbnailUrl = getThumbnailUrl(crawlingRequest.thumbnailImageUrl),
            imageUrlList = imageUrlListResponse?.get("data")?.get("similar")?.get(0)?.get("recommendedGoodsList")?.map { it.get("imageUrl").asText() } ?: emptyList(),
            firstCategory = crawlingRequest.category.categoryDepth1Name,
            secondCategory = crawlingRequest.category.categoryDepth2Name,
            price = crawlingRequest.goodsPrice.normalPrice,
            store = Store.MUSINSA,
            firstOptions = firstOptions, // 추가된 부분
            secondOptions = secondOptions, // 추가된 부분
            thirdOptions = thirdOptions // 추가된 부분
        )
    }

    private fun extractJsonData(scriptContent: String): String? {
        var jsonString: String? = null

        // 자바스크립트 내 변수 선언 패턴
        val pattern = "window.__MSS__.product.state = "
        // 패턴의 시작 위치 찾기
        val startIndex = scriptContent.indexOf(pattern)

        if (startIndex != -1) {
            // 패턴 이후 부분 추출
            val substring = scriptContent.substring(startIndex + pattern.length)
            // JSON 데이터의 끝 위치 찾기
            val endIndex = substring.indexOf("};") + 1
            // JSON 문자열 추출
            jsonString = substring.substring(0, endIndex)
        }

        return jsonString
    }

    private fun getOptionUrl(goodsNo: Long): String {
        return "https://goods.musinsa.com/api2/review/v1/view/filter?goodsNo=${goodsNo}"
    }

    private fun getThumbnailUrl(thumbnailUrl: String): String {
        return "https://image.msscdn.net/thumbnails${thumbnailUrl}"
    }

    // null 허용을 고려해 보자!
    data class CrawlingGatewayRequest(
        @SerializedName("goodsNo") val goodsNo: Long,
        @SerializedName("goodsNm") val goodsNm: String,
        @SerializedName("thumbnailImageUrl") val thumbnailImageUrl: String,
        @SerializedName("brandInfo") val brandInfo: BrandInfo,
        @SerializedName("category") val category: Category,
        @SerializedName("goodsPrice") val goodsPrice: GoodsPrice
    )

    data class BrandInfo(
        @SerializedName("brandName") val brandName: String,
    )

    data class Category(
        @SerializedName("categoryDepth1Name") val categoryDepth1Name: String,
        @SerializedName("categoryDepth2Name") val categoryDepth2Name: String,
    )

    data class GoodsPrice(
        @SerializedName("normalPrice") val normalPrice: Int,
    )

    data class FilterOption(
        val optionCount: Int,
        val firstOptions: List<Option> = emptyList(),
        val secondOptions: List<Option> = emptyList(),
        val thirdOptions: List<Option> = emptyList(),
        val filterText: String
    )

    data class Option(
        @SerializedName("val") val value: String,
        val txt: String,
        val qty: Int,
        val memo: String?,
        val title: String,
        val qnaTitle: String?,
        val restockYn: String?
    )


    data class MyProfileInfo(
        val hasMySize: Boolean,
        val myFilterEnum: String,
        val searchMemberInfo: Any?,
        val weight: Int,
        val height: Int,
        val skinInfoList: Any?,
        val filterText: String,
        val heightStart: Int,
        val heightEnd: Int,
        val weightStart: Int,
        val weightEnd: Int
    )

    data class ResponseData(
        val filterOption: FilterOption,
        val myProfileInfo: MyProfileInfo
    )

    data class Meta(
        val result: String,
        val errorCode: String?,
        val message: String?
    )

    data class ApiResponse(
        val data: ResponseData,
        val meta: Meta
    )

}