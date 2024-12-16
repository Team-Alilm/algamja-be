package org.team_alilm.application.service

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
        val optionResponse = restTemplate.getForEntity(optionUrl, OptionResponse::class.java).body

        val basicOptions = optionResponse?.data?.basic ?: emptyList()
        val firstOptionName = basicOptions.getOrNull(0)?.name ?: ""
        val secondOptionName = basicOptions.getOrNull(1)?.name ?: ""
        val thirdOptionName = basicOptions.getOrNull(2)?.name ?: ""
        val firstOptions = basicOptions.getOrNull(0)?.optionValues?.map { it.name } ?: emptyList()
        val secondOptions = basicOptions.getOrNull(1)?.optionValues?.map { it.name } ?: emptyList()
        val thirdOptions = basicOptions.getOrNull(2)?.optionValues?.map { it.name } ?: emptyList()

        return ProductCrawlingUseCase.CrawlingResult(
            number = crawlingRequest.goodsNo,
            name = crawlingRequest.goodsNm,
            brand = crawlingRequest.brandInfo.brandName,
            thumbnailUrl = crawlingRequest.thumbnailImageUrl,
            firstCategory = crawlingRequest.category.categoryDepth1Name,
            secondCategory = crawlingRequest.category.categoryDepth2Name,
            price = crawlingRequest.goodsPrice.normalPrice,
            store = Store.MUSINSA,
            firstOptionName = firstOptionName,
            secondOptionName = secondOptionName,
            thirdOptionName = thirdOptionName,
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
        return "https://goods-detail.musinsa.com/api2/goods/${goodsNo}/options?goodsSaleType=SALE"
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

    data class OptionResponse(
        val meta: Meta,
        val data: OptionData,
        val error: Any? // 에러가 null이므로 Any? 타입으로 설정
    )

    data class Meta(
        val result: String,
        val errorCode: String,
        val message: String
    )

    data class OptionData(
        val basic: List<Basic>,
        val extra: List<Any>, // extra는 비어있는 리스트이므로 Any로 설정
        val optionItems: List<OptionItem>
    )

    data class Basic(
        val no: Long,
        val type: String,
        val displayType: String,
        val name: String,
        val standardOptionNo: Long,
        val sequence: Int,
        val isDeleted: Boolean,
        val optionValues: List<OptionValue>
    )

    data class OptionValue(
        val no: Long,
        val optionNo: Long,
        val name: String,
        val code: String,
        val sequence: Int,
        val standardOptionValueNo: Long,
        val color: String?,
        val isDeleted: Boolean
    )

    data class OptionItem(
        val no: Long,
        val goodsNo: Long,
        val optionValueNos: List<Long>,
        val managedCode: String,
        val price: Int,
        val activated: Boolean,
        val outOfStock: Boolean,
        val isDeleted: Boolean,
        val optionValues: List<OptionValueDetail>,
        val colors: List<Color>,
        val remainQuantity: Int
    )

    data class OptionValueDetail(
        val no: Long,
        val name: String,
        val code: String,
        val optionNo: Long,
        val optionName: String
    )

    data class Color(
        val optionItemNo: Long,
        val colorCode: String,
        val colorType: String
    )
}