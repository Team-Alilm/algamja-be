package org.team_alilm.algamja.product.crawler.impl.ably

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import org.team_alilm.algamja.common.exception.BusinessException
import org.team_alilm.algamja.common.exception.ErrorCode
import org.team_alilm.algamja.product.crawler.ProductCrawler
import org.team_alilm.algamja.product.crawler.dto.CrawledProduct
import org.team_alilm.algamja.product.crawler.impl.ably.dto.AblyApiResponse
import org.team_alilm.algamja.product.crawler.impl.ably.dto.AblyOptionsResponse
import org.team_alilm.algamja.product.crawler.impl.ably.dto.AblyOptionComponent
import org.team_alilm.algamja.product.crawler.util.CategoryMapper
import org.team_alilm.algamja.common.enums.Store
import org.team_alilm.algamja.common.enums.ProductCategory
import java.math.BigDecimal
import java.net.URI
import java.util.regex.Pattern

@Component
class AblyCrawler(
    private val restClient: RestClient,
    private val ablyTokenManager: AblyTokenManager
) : ProductCrawler {

    private val log = LoggerFactory.getLogger(javaClass)
    private val goodsUrlPattern = Pattern.compile(""".*/goods/(\d+).*""")
    
    override fun supports(url: String): Boolean {
        return runCatching {
            val uri = URI(url.trim())
            val host = uri.host?.lowercase()
            val isSupported = (host == "a-bly.com" || host == "m.a-bly.com" || host?.endsWith(".a-bly.com") == true) &&
                    goodsUrlPattern.matcher(url).matches()
            
            log.trace("URL support check: url={}, host={}, supported={}", url, host, isSupported)
            isSupported
        }.getOrElse { 
            log.debug("Failed to parse URL for support check: {}", url)
            false 
        }
    }
    
    override fun normalize(url: String): String {
        return runCatching {
            val uri = URI(url.trim())
            val scheme = (uri.scheme ?: "https").lowercase()
            val host = uri.host?.lowercase()?.let {
                if (it == "m.a-bly.com") "a-bly.com" else it
            } ?: return url.substringBefore("?")
            
            val path = uri.path ?: ""
            val normalizedUrl = "$scheme://$host$path".substringBefore("?")
            
            log.debug("URL normalization: {} -> {}", url, normalizedUrl)
            normalizedUrl
        }.getOrElse { 
            val fallback = url.substringBefore("?")
            log.debug("Failed to normalize URL, using fallback: {} -> {}", url, fallback)
            fallback
        }
    }
    
    override fun fetch(url: String): CrawledProduct {
        val startTime = System.currentTimeMillis()
        val goodsId = extractGoodsId(url)
        
        log.info("Starting Ably product crawling for goodsId: {}, url: {}", goodsId, url)
        
        val token = ablyTokenManager.getToken()
        val apiUrl = "https://api.a-bly.com/api/v3/goods/$goodsId/basic/?channel=0"
        
        log.debug("Fetching basic product info from API: {}", apiUrl)
        
        val response = fetchWithRetry(apiUrl, token, goodsId)
        
        val goods = response.goods ?: run {
            log.error("Goods data is null in API response for goodsId: {}", goodsId)
            throw BusinessException(ErrorCode.CRAWLER_INVALID_RESPONSE)
        }
        
        log.debug("Successfully fetched product: name='{}', brand='{}'", goods.name, goods.market?.name)
        
        val thumbnailUrl = goods.coverImages?.firstOrNull() ?: ""
        val imageUrls = goods.coverImages ?: emptyList()
        val categoryName = goods.displayCategories?.firstOrNull()?.name ?: ""
        
        log.debug("Product details: images={}, category='{}', price={}", 
                 imageUrls.size, categoryName, goods.priceInfo?.thumbnailPrice)
        
        // Fetch options using optimized recursive approach
        val optionsData = fetchOptionsData(goodsId, token)
        val (firstOptions, secondOptions, thirdOptions) = extractAllOptionsRecursively(goodsId, token, optionsData)
        
        // 한국어 카테고리를 영어로 변환
        val koreanCategory = CategoryMapper.mapCategory(categoryName)
        val englishFirstCategory = ProductCategory.mapKoreanToEnglish(koreanCategory) ?: "OTHERS"
        val englishSecondCategory = ProductCategory.mapKoreanToEnglish(categoryName)
        
        val crawledProduct = CrawledProduct(
            storeNumber = goods.sno,
            name = goods.name,
            brand = goods.market?.name ?: "Unknown",
            thumbnailUrl = thumbnailUrl,
            imageUrls = imageUrls,
            store = Store.ABLY,
            price = BigDecimal.valueOf(goods.priceInfo?.thumbnailPrice ?: 0),
            firstCategory = englishFirstCategory,
            secondCategory = englishSecondCategory,
            firstOptions = firstOptions,
            secondOptions = secondOptions,
            thirdOptions = thirdOptions
        )
        
        val duration = System.currentTimeMillis() - startTime
        val totalCombinations = when {
            firstOptions.isEmpty() -> 0
            secondOptions.isEmpty() && thirdOptions.isEmpty() -> firstOptions.size
            secondOptions.isEmpty() -> firstOptions.size * thirdOptions.size
            thirdOptions.isEmpty() -> firstOptions.size * secondOptions.size
            else -> firstOptions.size * secondOptions.size * thirdOptions.size
        }
        
        log.info("Successfully crawled Ably product goodsId: {} in {}ms, options: {}/{}/{} (total combinations: {})", 
                goodsId, duration, firstOptions.size, secondOptions.size, thirdOptions.size, totalCombinations)
        
        return crawledProduct
    }
    
    private fun extractGoodsId(url: String): Long {
        val matcher = goodsUrlPattern.matcher(url)
        return if (matcher.matches()) {
            val goodsId = matcher.group(1).toLong()
            log.debug("Extracted goodsId: {} from URL: {}", goodsId, url)
            goodsId
        } else {
            log.error("Failed to extract goodsId from URL: {}", url)
            throw BusinessException(ErrorCode.CRAWLER_INVALID_URL)
        }
    }

    private fun fetchOptionsData(goodsId: Long, token: String): AblyOptionsResponse? {
        return try {
            val optionsUrl = "https://api.a-bly.com/api/v2/goods/$goodsId/options/?depth=1"
            log.debug("Fetching options data from API: {}", optionsUrl)
            
            val response = restClient.get()
                .uri(optionsUrl)
                .header("x-anonymous-token", token)
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                .retrieve()
                .body(AblyOptionsResponse::class.java)
            
            val optionCount = response?.optionComponents?.size ?: 0
            log.debug("Successfully fetched {} options for goodsId: {}", optionCount, goodsId)
            
            response
        } catch (e: Exception) {
            log.debug("Failed to fetch options for goodsId: {} (options are optional): {}", goodsId, e.message)
            null
        }
    }
    
    private fun extractOptionName(fullName: String): String {
        val extracted = fullName.substringAfterLast('_').takeIf { it.isNotBlank() } ?: fullName
        log.trace("Option name extraction: '{}' -> '{}'", fullName, extracted)
        return extracted
    }
    
    /**
     * 재귀적으로 모든 레벨의 옵션을 추출하여 개별 리스트로 반환
     * @param goodsId 상품 ID
     * @param token 인증 토큰
     * @param optionsData 첫 번째 레벨 옵션 데이터
     * @return Triple<firstOptions, secondOptions, thirdOptions>
     */
    private fun extractAllOptionsRecursively(goodsId: Long, token: String, optionsData: AblyOptionsResponse?): Triple<List<String>, List<String>, List<String>> {
        if (optionsData?.optionComponents == null) {
            log.debug("No option components found, returning empty options")
            return Triple(emptyList(), emptyList(), emptyList())
        }
        
        val firstOptions = mutableSetOf<String>()
        val secondOptions = mutableSetOf<String>()
        val thirdOptions = mutableSetOf<String>()
        
        // Process each first-level option component
        optionsData.optionComponents.forEach { component ->
            processFirstLevelOption(goodsId, token, component, firstOptions, secondOptions, thirdOptions)
        }
        
        log.debug("Recursively extracted options - first: {}, second: {}, third: {}", 
                 firstOptions.size, secondOptions.size, thirdOptions.size)
        
        return Triple(firstOptions.toList(), secondOptions.toList(), thirdOptions.toList())
    }
    
    private fun processFirstLevelOption(
        goodsId: Long, 
        token: String, 
        component: AblyOptionComponent, 
        firstOptions: MutableSet<String>,
        secondOptions: MutableSet<String>,
        thirdOptions: MutableSet<String>
    ) {
        val optionName = extractOptionName(component.name)
        firstOptions.add(optionName)
        
        if (component.isFinalDepth != false) return
        
        val nestedOptions = fetchNestedOptionsQuick(goodsId, token, component.goodsOptionSno, 2) ?: return
        nestedOptions.optionComponents?.forEach { nestedComponent ->
            processSecondLevelOption(goodsId, token, nestedComponent, secondOptions, thirdOptions)
        }
    }
    
    private fun processSecondLevelOption(
        goodsId: Long,
        token: String,
        nestedComponent: AblyOptionComponent,
        secondOptions: MutableSet<String>,
        thirdOptions: MutableSet<String>
    ) {
        val nestedOptionName = extractOptionName(nestedComponent.name)
        
        when (nestedComponent.depth) {
            2 -> secondOptions.add(nestedOptionName)
            3 -> thirdOptions.add(nestedOptionName)
            else -> secondOptions.add(nestedOptionName)
        }
        
        if (!nestedComponent.isFinalDepth) {
            val thirdLevelOptions = fetchNestedOptionsQuick(goodsId, token, nestedComponent.goodsOptionSno, 3) ?: return
            thirdLevelOptions.optionComponents?.forEach { thirdComponent ->
                val thirdOptionName = extractOptionName(thirdComponent.name)
                thirdOptions.add(thirdOptionName)
            }
        }
    }
    
    /**
     * 특정 옵션이 선택된 상태에서 하위 옵션들을 빠르게 가져옴 (타임아웃 적용)
     */
    private fun fetchNestedOptionsQuick(goodsId: Long, token: String, selectedOptionSno: Long, depth: Int = 1): AblyOptionsResponse? {
        return try {
            val nestedOptionsUrl = "https://api.a-bly.com/api/v2/goods/$goodsId/options/?depth=$depth&selected_option_sno=$selectedOptionSno"
            
            val response = restClient.get()
                .uri(nestedOptionsUrl)
                .header("x-anonymous-token", token)
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                .retrieve()
                .body(AblyOptionsResponse::class.java)
            
            response
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * 403 에러 시 토큰 갱신 후 재시도 로직
     */
    private fun fetchWithRetry(apiUrl: String, initialToken: String, goodsId: Long): AblyApiResponse {
        return try {
            restClient.get()
                .uri(apiUrl)
                .header("x-anonymous-token", initialToken)
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                .retrieve()
                .body(AblyApiResponse::class.java)
                ?: throw BusinessException(ErrorCode.CRAWLER_INVALID_RESPONSE)
        } catch (e: Exception) {
            when {
                e.message?.contains("403") == true -> {
                    log.warn("Authentication failed (403 Forbidden) for goodsId: {}. Trying token refresh and retry...", goodsId)
                    
                    // 토큰 강제 갱신 후 재시도
                    try {
                        val newToken = ablyTokenManager.forceRefreshToken()
                        log.info("Token refreshed for goodsId: {}, retrying with new token", goodsId)
                        Thread.sleep(1000) // 1초 대기 후 재시도
                        
                        restClient.get()
                            .uri(apiUrl)
                            .header("x-anonymous-token", newToken)
                            .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                            .retrieve()
                            .body(AblyApiResponse::class.java)
                            ?: throw BusinessException(ErrorCode.CRAWLER_INVALID_RESPONSE)
                            
                    } catch (retryException: Exception) {
                        log.error("Failed even after token refresh for goodsId: {}. Error: {}", goodsId, retryException.message)
                        throw BusinessException(ErrorCode.CRAWLER_INVALID_RESPONSE)
                    }
                }
                e.message?.contains("429") == true -> {
                    log.warn("Rate limit exceeded (429) for goodsId: {}. Will retry later: {}", goodsId, e.message)
                    throw BusinessException(ErrorCode.CRAWLER_INVALID_RESPONSE)
                }
                e.message?.contains("503") == true || e.message?.contains("502") == true -> {
                    log.warn("Server temporarily unavailable for goodsId: {}. Error: {}", goodsId, e.message)
                    throw BusinessException(ErrorCode.CRAWLER_INVALID_RESPONSE)
                }
                else -> {
                    log.error("Failed to fetch basic product info for goodsId: {}", goodsId, e)
                    throw BusinessException(ErrorCode.CRAWLER_INVALID_RESPONSE)
                }
            }
        }
    }
    
}