package org.team_alilm.product.crawler.impl.musinsa

import com.fasterxml.jackson.databind.ObjectMapper
import org.jsoup.Jsoup
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import org.team_alilm.common.exception.BusinessException
import org.team_alilm.common.exception.ErrorCode
import org.team_alilm.product.crawler.ProductCrawler
import org.team_alilm.product.crawler.dto.CrawledProduct
import org.team_alilm.product.crawler.impl.musinsa.dto.option.OptionApiResponse
import org.team_alilm.product.crawler.impl.musinsa.dto.ProductState
import org.team_alilm.product.crawler.util.CategoryMapper
import java.net.IDN
import java.net.URI

@Component
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
    // ─────────────────────────────────────────────────────────────────────────────
    // ✅ 강화된 도메인 검증 + 정규화
    // ─────────────────────────────────────────────────────────────────────────────
    override fun supports(url: String): Boolean =
        runCatching {
            val u = URI(url.trim())
            val scheme = u.scheme?.lowercase()
            val host = normalizeHost(u.host) ?: return false
            (scheme == "http" || scheme == "https") &&
                    (host == "musinsa.com" || host.endsWith(".musinsa.com"))
        }.getOrDefault(false)

    override fun normalize(url: String): String =
        runCatching {
            val u = URI(url.trim())
            val scheme = (u.scheme ?: "https").lowercase()
            val host = normalizeHost(u.host) ?: return url.substringBefore("?")
            val path = u.rawPath.orEmpty()
            if ((scheme == "http" || scheme == "https") &&
                (host == "musinsa.com" || host.endsWith(".musinsa.com"))
            ) {
                // 쿼리/프래그먼트 제거, 표준 포트는 생략
                val port = when (u.port) {
                    -1, 80, 443 -> ""
                    else -> ":${u.port}"   // 비표준 포트 허용하지 않으려면 supports에서 false 처리
                }
                "$scheme://$host$port$path"
            } else url.substringBefore("?")
        }.getOrElse { url.substringBefore("?") }

    private fun normalizeHost(raw: String?): String? {
        if (raw.isNullOrBlank()) return null
        // 끝의 점 제거 + 소문자 + 퓨니코드 정규화
        val trimmed = raw.trim().trimEnd('.').lowercase()
        return IDN.toASCII(trimmed)
    }


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

        // 4) 옵션 API 호출 → 옵션명 리스트 추출
        val option = fetchOptionData(state.goodsNo) // OptionApiResponse
        val basic = option.data?.basic.orEmpty()    // depth별 옵션 배열

        val firstOptions  = basic.getOrNull(0)?.optionValues?.map { it.name } ?: emptyList()
        val secondOptions = basic.getOrNull(1)?.optionValues?.map { it.name } ?: emptyList()
        val thirdOptions  = basic.getOrNull(2)?.optionValues?.map { it.name } ?: emptyList()

        return CrawledProduct(
            storeNumber    = state.goodsNo,
            name           = state.goodsNm,
            brand          = state.brandInfo.brandName,
            thumbnailUrl   = toAbsoluteImageUrl(state.thumbnailImageUrl),
            imageUrls      = imageUrls,
            store          = "MUSINSA", // enum 변환은 호출부/서비스 단에서 해도 OK
            price          = price,
            firstCategory  = CategoryMapper.mapCategory(state.category.categoryDepth1Name),
            secondCategory = state.category.categoryDepth2Name,
            firstOptions   = firstOptions,
            secondOptions  = secondOptions,
            thirdOptions   = thirdOptions
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

    /** 옵션 API 호출 */
    private fun fetchOptionData(goodsNo: Long): OptionApiResponse {
        val uri = "https://goods-detail.musinsa.com/api2/goods/$goodsNo/v2/options?goodsSaleType=SALE"
        return restClient.get()
            .uri(uri)
            .retrieve()
            .body(OptionApiResponse::class.java)
            ?: throw BusinessException(ErrorCode.MUSINSA_INVALID_RESPONSE)
    }
}