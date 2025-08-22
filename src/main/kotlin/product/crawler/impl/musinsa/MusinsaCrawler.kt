package org.team_alilm.product.crawler.impl.musinsa

import com.fasterxml.jackson.databind.ObjectMapper
import org.jsoup.Jsoup
import org.springframework.core.ParameterizedTypeReference
import org.springframework.web.client.RestClient
import org.team_alilm.common.exception.BusinessException
import org.team_alilm.common.exception.ErrorCode
import org.team_alilm.product.crawler.ProductCrawler
import org.team_alilm.product.crawler.dto.CrawledProduct
import org.team_alilm.product.crawler.impl.musinsa.dto.OptionApiResponse
import org.team_alilm.product.crawler.impl.musinsa.dto.ProductState

class MusinsaCrawler(
    private val objectMapper: ObjectMapper,
    private val restClient: RestClient,
) : ProductCrawler {

    private val IMAGE_BASE_URL = "https://image.msscdn.net/thumbnails"

    // window.__MSS__.product.state = { ... };
    private val stateRegex = Regex(
        pattern = """window\.__MSS__\.product\.state\s*=\s*(\{.*?});""",
        options = setOf(RegexOption.DOT_MATCHES_ALL)
    )

    override fun supports(url: String): Boolean =
        url.contains("musinsa.com", ignoreCase = true)

    override fun normalize(url: String): String =
        url.substringBefore("?") // 불필요한 쿼리 제거 (필요 시 로직 확장)

    override fun fetch(url: String): CrawledProduct {
        val normalized = normalize(url)

        // UA/Timeout 지정 (Jsoup 내부에서)
        val html = Jsoup.connect(normalized)
            .userAgent("Mozilla/5.0 (compatible; AlilmBot/1.0)")
            .timeout(10_000)
            .get()
            .html()

        // 1) JSON 블럭 추출
        val json = extractProductStateJson(html)

        // 2) DTO 매핑 (스프링 제공 ObjectMapper)
        val state = objectMapper.readValue(json, ProductState::class.java)

        // 3) 이미지/가격/카테고리 가공
        val imageUrls = state.goodsImages
            .map { toAbsoluteImageUrl(it.imageUrl) }
            .distinct()

        val price = state.goodsPrice.salePrice
            .toBigDecimal()

        return CrawledProduct(
            storeNumber    = state.goodsNo,
            name           = state.goodsNm,
            brand          = state.brandInfo.brandName,
            thumbnailUrl   = toAbsoluteImageUrl(state.thumbnailImageUrl),
            imageUrls      = imageUrls,
            store          = "MUSINSA", // enum 변환은 호출부/서비스 단에서 해도 OK
            price          = price,
            firstCategory  = state.category.categoryDepth1Name,
            secondCategory = state.category.categoryDepth2Name,
            firstOption    = null,
            secondOption   = null,
            thirdOption    = null
        )
    }

    private fun extractProductStateJson(html: String): String {
        val matchResult = stateRegex.find(html)
            ?: throw BusinessException(ErrorCode.MUSINSA_INVALID_RESPONSE)
        return matchResult.groupValues[1]
    }

    private fun toAbsoluteImageUrl(relativeUrl: String?): String {
        if (relativeUrl.isNullOrBlank()) return ""
        return if (relativeUrl.startsWith("http")) {
            relativeUrl
        } else {
            IMAGE_BASE_URL + relativeUrl
        }
    }

    private fun fetchOptionData(goodsNo: Long): OptionApiResponse {
        val optionUri = "https://goods-detail.musinsa.com/api2/goods/$goodsNo/v2/options?goodsSaleType=SALE"
        val response: OptionApiResponse? = restClient.get()
            .uri(optionUri)
            .retrieve()
            .body(object : ParameterizedTypeReference<OptionApiResponse>() {})

        return response ?: throw BusinessException(ErrorCode.MUSINSA_INVALID_RESPONSE)

    }
}