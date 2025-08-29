package org.team_alilm.algamja.product.crawler.impl.cm29

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import org.team_alilm.algamja.common.exception.BusinessException
import org.team_alilm.algamja.common.exception.ErrorCode
import org.team_alilm.algamja.product.crawler.ProductCrawler
import org.team_alilm.algamja.product.crawler.dto.CrawledProduct
import org.team_alilm.algamja.product.crawler.impl.cm29.dto.CM29ApiResponse
import org.team_alilm.algamja.product.crawler.util.CategoryMapper
import java.net.IDN
import java.net.URI

@Component
class CM29Crawler(
    private val restClient: RestClient,
) : ProductCrawler {

    private val log = LoggerFactory.getLogger(javaClass)
    private val API_BASE_URL = "https://bff-api.29cm.co.kr/api/v5/product-detail"
    private val IMAGE_BASE_URL = "https://image.29cm.co.kr"

    override fun supports(url: String): Boolean =
        runCatching {
            val u = URI(url.trim())
            val scheme = u.scheme?.lowercase()
            val host = normalizeHost(u.host) ?: return false
            (scheme == "http" || scheme == "https") &&
                    (host == "29cm.co.kr" || host.endsWith(".29cm.co.kr"))
        }.getOrDefault(false)

    override fun normalize(url: String): String =
        runCatching {
            val u = URI(url.trim())
            val scheme = (u.scheme ?: "https").lowercase()
            val host = normalizeHost(u.host) ?: return url.substringBefore("?")
            val path = u.rawPath.orEmpty()
            if ((scheme == "http" || scheme == "https") &&
                (host == "29cm.co.kr" || host.endsWith(".29cm.co.kr"))
            ) {
                val port = when (u.port) {
                    -1, 80, 443 -> ""
                    else -> ":${u.port}"
                }
                "$scheme://$host$port$path"
            } else url.substringBefore("?")
        }.getOrElse { url.substringBefore("?") }

    private fun normalizeHost(raw: String?): String? {
        if (raw.isNullOrBlank()) return null
        val trimmed = raw.trim().trimEnd('.').lowercase()
        return IDN.toASCII(trimmed)
    }

    override fun fetch(url: String): CrawledProduct {
        val normalized = normalize(url)
        val productId = extractProductId(normalized)

        val response = try {
            restClient.get()
                .uri("$API_BASE_URL/$productId")
                .header("User-Agent", "Mozilla/5.0 (compatible; AlilmBot/1.0)")
                .retrieve()
                .body(CM29ApiResponse::class.java)
                ?: throw BusinessException(ErrorCode.CM29_INVALID_RESPONSE)
        } catch (e: Exception) {
            log.error("Failed to fetch CM29 product data", e)
            throw BusinessException(ErrorCode.CM29_INVALID_RESPONSE, cause = e)
        }

        if (response.result != "SUCCESS" || response.data == null) {
            throw BusinessException(ErrorCode.CM29_INVALID_RESPONSE)
        }

        val data = response.data

        val imageUrls = data.itemImages
            .filter { it.imageType == 3 || it.imageType == 4 } // 상품 이미지만
            .sortedBy { it.orderingIdx }
            .map { toAbsoluteImageUrl(it.imageUrl) }
            .distinct()

        val thumbnailUrl = imageUrls.firstOrNull() ?: ""

        val brandName = data.frontBrand.brandNameKor?.takeIf { it.isNotBlank() }
            ?: data.frontBrand.brandNameEng?.takeIf { it.isNotBlank() }
            ?: "브랜드 없음"

        val finalPrice = data.sellPrice

        val categories = data.frontCategoryInfo
        val allCategoryNames = categories.flatMap { category ->
            listOfNotNull(
                category.category1Name,
                category.category2Name,
                category.category3Name
            )
        }.joinToString(" ")
        val firstCategory = CategoryMapper.mapCategory(allCategoryNames)
        val secondCategory = categories.firstOrNull()?.category2Name

        val options = data.optionItems?.list ?: emptyList()
        val firstOptions = options.getOrNull(0)?.optionValueList?.mapNotNull { it.optionValue } ?: emptyList()
        val secondOptions = options.getOrNull(1)?.optionValueList?.mapNotNull { it.optionValue } ?: emptyList()
        val thirdOptions = options.getOrNull(2)?.optionValueList?.mapNotNull { it.optionValue } ?: emptyList()

        return CrawledProduct(
            storeNumber = data.itemNo,
            name = data.itemName.takeIf { it.isNotBlank() } ?: "상품명 없음",
            brand = brandName,
            thumbnailUrl = thumbnailUrl,
            imageUrls = imageUrls,
            store = "29CM",
            price = finalPrice.toBigDecimal(),
            firstCategory = firstCategory,
            secondCategory = secondCategory,
            firstOptions = firstOptions,
            secondOptions = secondOptions,
            thirdOptions = thirdOptions
        )
    }

    private fun extractProductId(url: String): String {
        val regex = Regex("/products/(\\d+)")
        val matchResult = regex.find(url)
            ?: throw BusinessException(ErrorCode.CM29_INVALID_RESPONSE)
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
}