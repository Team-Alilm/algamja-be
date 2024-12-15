package org.team_alilm.application.service

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.team_alilm.application.port.`in`.use_case.product.crawling.ProductCrawlingUseCase
import org.team_alilm.application.port.out.gateway.crawling.CrawlingGateway
import org.team_alilm.application.port.out.gateway.crawling.CrawlingGateway.*
import org.team_alilm.domain.product.Store
import org.team_alilm.global.error.NotFoundStoreException

@Service
@Transactional(readOnly = true)
class MusinsaProductCrawlingService(
    private val crawlingGateway: CrawlingGateway,
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

        return ProductCrawlingUseCase.CrawlingResult(
            number = crawlingRequest.goodsNo, // 필요하면 CrawlingGatewayRequest에 필드를 추가
            name = crawlingRequest.goodsNm, // 필요하면 CrawlingGatewayRequest에 필드를 추가
            brand = crawlingRequest.brandInfo.brandName, // 필요하면 CrawlingGatewayRequest에 필드를 추가
            thumbnailUrl = crawlingRequest.thumbnailImageUrl, // 필요하면 CrawlingGatewayRequest에 필드를 추가
            firstCategory = crawlingRequest.category.categoryDepth1Name, // 필요하면 CrawlingGatewayRequest에 필드를 추가
            secondCategory = crawlingRequest.category.categoryDepth2Name, // 필요하면 CrawlingGatewayRequest에 필드를 추가
            price = crawlingRequest.goodsPrice.normalPrice, // 필요하면 CrawlingGatewayRequest에 필드를 추가
            store = Store.MUSINSA,
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
}