package org.team_alilm.product.crawler.impl.ably

import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import org.team_alilm.common.exception.BusinessException
import common.exception.ErrorCode
import org.team_alilm.product.crawler.ProductCrawler
import org.team_alilm.product.crawler.dto.CrawledProduct
import org.team_alilm.product.crawler.impl.ably.dto.AblyApiResponse
import org.team_alilm.product.crawler.impl.ably.dto.AblyOptionsResponse
import org.team_alilm.product.crawler.util.CategoryMapper
import java.math.BigDecimal
import java.net.URI
import java.util.regex.Pattern

@Component
class AblyCrawler(
    private val restClient: RestClient,
    private val ablyTokenManager: AblyTokenManager
) : ProductCrawler {

    private val goodsUrlPattern = Pattern.compile(""".*/goods/(\d+).*""")
    
    override fun supports(url: String): Boolean {
        return runCatching {
            val uri = URI(url.trim())
            val host = uri.host?.lowercase()
            (host == "a-bly.com" || host == "m.a-bly.com" || host?.endsWith(".a-bly.com") == true) &&
                    goodsUrlPattern.matcher(url).matches()
        }.getOrDefault(false)
    }
    
    override fun normalize(url: String): String {
        return runCatching {
            val uri = URI(url.trim())
            val scheme = (uri.scheme ?: "https").lowercase()
            val host = uri.host?.lowercase()?.let {
                if (it == "m.a-bly.com") "a-bly.com" else it
            } ?: return url.substringBefore("?")
            
            val path = uri.path ?: ""
            "$scheme://$host$path".substringBefore("?")
        }.getOrElse { url.substringBefore("?") }
    }
    
    override fun fetch(url: String): CrawledProduct {
        val goodsId = extractGoodsId(url)
        val token = ablyTokenManager.getToken()
        
        val apiUrl = "https://api.a-bly.com/api/v3/goods/$goodsId/basic/?channel=0"
        
        val response = restClient.get()
            .uri(apiUrl)
            .header("x-anonymous-token", token)
            .header("User-Agent", "Mozilla/5.0 (compatible; AlilmBot/1.0)")
            .retrieve()
            .body(AblyApiResponse::class.java)
            ?: throw BusinessException(ErrorCode.CRAWLER_INVALID_RESPONSE)
        
        val goods = response.goods
            ?: throw BusinessException(ErrorCode.CRAWLER_INVALID_RESPONSE)
        
        val thumbnailUrl = goods.coverImages?.firstOrNull() ?: ""
        val imageUrls = goods.coverImages ?: emptyList()
        
        val categoryName = goods.displayCategories?.firstOrNull()?.name ?: ""
        
        // Fetch options
        val optionsData = fetchOptionsData(goodsId, token)
        val firstOptions = optionsData?.optionComponents?.map { extractOptionName(it.name) } ?: emptyList()
        
        return CrawledProduct(
            storeNumber = goods.sno,
            name = goods.name,
            brand = goods.market?.name ?: "Unknown",
            thumbnailUrl = thumbnailUrl,
            imageUrls = imageUrls,
            store = "ABLY",
            price = BigDecimal.valueOf(goods.priceInfo?.thumbnailPrice ?: 0),
            firstCategory = CategoryMapper.mapCategory(categoryName),
            secondCategory = categoryName,
            firstOptions = firstOptions,
            secondOptions = emptyList(),
            thirdOptions = emptyList()
        )
    }
    
    private fun extractGoodsId(url: String): Long {
        val matcher = goodsUrlPattern.matcher(url)
        return if (matcher.matches()) {
            matcher.group(1).toLong()
        } else {
            throw BusinessException(ErrorCode.CRAWLER_INVALID_URL)
        }
    }
    
    private fun fetchOptionsData(goodsId: Long, token: String): AblyOptionsResponse? {
        return try {
            val optionsUrl = "https://api.a-bly.com/api/v2/goods/$goodsId/options/?depth=1"
            restClient.get()
                .uri(optionsUrl)
                .header("x-anonymous-token", token)
                .header("User-Agent", "Mozilla/5.0 (compatible; AlilmBot/1.0)")
                .retrieve()
                .body(AblyOptionsResponse::class.java)
        } catch (e: Exception) {
            // Options are optional, return null if not available
            null
        }
    }
    
    private fun extractOptionName(fullName: String): String {
        // Extract clean option name from "[PT385] 벤티 핀턱 밴딩 팬츠_화이트" -> "화이트"
        return fullName.substringAfterLast('_').takeIf { it.isNotBlank() } ?: fullName
    }
    
}