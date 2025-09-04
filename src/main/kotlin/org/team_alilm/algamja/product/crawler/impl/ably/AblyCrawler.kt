package org.team_alilm.algamja.product.crawler.impl.ably

import org.slf4j.LoggerFactory
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import org.team_alilm.algamja.common.exception.BusinessException
import org.team_alilm.algamja.common.exception.ErrorCode
import org.team_alilm.algamja.product.crawler.ProductCrawler
import org.team_alilm.algamja.product.crawler.dto.CrawledProduct
import org.team_alilm.algamja.product.crawler.impl.ably.dto.AblyApiResponse
import org.team_alilm.algamja.product.crawler.impl.ably.dto.AblyOptionsResponse
import org.team_alilm.algamja.product.crawler.impl.ably.dto.AblyOptionComponent
import org.team_alilm.algamja.product.crawler.impl.ably.dto.AblyGoods
import org.team_alilm.algamja.product.crawler.util.CategoryMapper
import org.team_alilm.algamja.common.enums.Store
import org.team_alilm.algamja.common.enums.ProductCategory
import java.math.BigDecimal
import java.net.URI
import java.util.regex.Pattern

data class OptionProcessingContext(
    val goodsId: Long,
    val firstOptions: MutableSet<String>,
    val secondOptions: MutableSet<String>,
    val thirdOptions: MutableSet<String>
)

@Component("ablyCrawler")
@Order(10) // HybridCrawler보다 낮은 우선순위
class AblyCrawler(
    private val restClient: RestClient
) : ProductCrawler {

    companion object {
        private const val ANONYMOUS_TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhbm9ueW1vdXNfaWQiOiI1MjkwNzg3NTciLCJpYXQiOjE3NTYzNDM4ODh9.GG6bB2-q-cb47qD5UBwK5AQ4AzGLKSH3gZ0rsKWZR4Q"
        
        // 다양한 User-Agent 풀로 순환 사용
        private val USER_AGENTS = listOf(
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.0.0 Safari/537.36 Edg/119.0.0.0",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36",
            "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"
        )
        
        private const val DEFAULT_BRAND = "Unknown"
        private const val RETRY_DELAY_MS = 2000L
        private const val MAX_RETRY_COUNT = 3
        private const val FIRST_LEVEL_DEPTH = 1
        private const val SECOND_LEVEL_DEPTH = 2
        private const val THIRD_LEVEL_DEPTH = 3
        private const val BASIC_API_URL_TEMPLATE = "https://api.a-bly.com/api/v3/goods/%d/basic/?channel=0"
        private const val OPTIONS_API_URL_TEMPLATE = "https://api.a-bly.com/api/v2/goods/%d/options/?depth=%d"
        private const val OPTIONS_WITH_SELECTION_URL_TEMPLATE = "https://api.a-bly.com/api/v2/goods/%d/options/?depth=%d&selected_option_sno=%d"
    }

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
        
        val basicProductData = fetchBasicProductData(goodsId)
        val productOptions = fetchProductOptions(goodsId)
        val crawledProduct = buildCrawledProduct(basicProductData, productOptions)
        
        logFetchCompletion(goodsId, startTime, productOptions)
        return crawledProduct
    }

    private fun fetchBasicProductData(goodsId: Long): AblyGoods {
        val apiUrl = BASIC_API_URL_TEMPLATE.format(goodsId)
        log.debug("Fetching basic product info from API: {}", apiUrl)
        
        val response = fetchWithRetry(apiUrl, goodsId)
        
        val goods = response.goods ?: run {
            log.error("Goods data is null in API response for goodsId: {}", goodsId)
            throw BusinessException(ErrorCode.CRAWLER_INVALID_RESPONSE)
        }
        
        log.debug("Successfully fetched product: name='{}', brand='{}'", goods.name, goods.market?.name)
        return goods
    }

    private fun fetchProductOptions(goodsId: Long): Triple<List<String>, List<String>, List<String>> {
        val optionsData = fetchOptionsData(goodsId)
        return extractAllOptionsRecursively(goodsId, optionsData)
    }

    private fun buildCrawledProduct(goods: AblyGoods, options: Triple<List<String>, List<String>, List<String>>): CrawledProduct {
        val (firstOptions, secondOptions, thirdOptions) = options
        val thumbnailUrl = goods.coverImages?.firstOrNull() ?: ""
        val imageUrls = goods.coverImages ?: emptyList()
        val categoryName = goods.displayCategories?.firstOrNull()?.name ?: ""
        
        log.debug("Product details: images={}, category='{}', price={}", 
                 imageUrls.size, categoryName, goods.priceInfo?.thumbnailPrice)
        
        val categories = mapCategories(categoryName)
        
        return CrawledProduct(
            storeNumber = goods.sno,
            name = goods.name,
            brand = goods.market?.name ?: DEFAULT_BRAND,
            thumbnailUrl = thumbnailUrl,
            imageUrls = imageUrls,
            store = Store.ABLY,
            price = BigDecimal.valueOf(goods.priceInfo?.thumbnailPrice ?: 0),
            firstCategory = categories.first,
            secondCategory = categories.second,
            firstOptions = firstOptions,
            secondOptions = secondOptions,
            thirdOptions = thirdOptions
        )
    }

    private fun mapCategories(categoryName: String): Pair<String, String?> {
        val koreanCategory = CategoryMapper.mapCategory(categoryName)
        val englishFirstCategory = ProductCategory.mapKoreanToEnglish(koreanCategory) ?: "OTHERS"
        val englishSecondCategory = ProductCategory.mapKoreanToEnglish(categoryName)
        return Pair(englishFirstCategory, englishSecondCategory)
    }

    private fun logFetchCompletion(goodsId: Long, startTime: Long, options: Triple<List<String>, List<String>, List<String>>) {
        val (firstOptions, secondOptions, thirdOptions) = options
        val duration = System.currentTimeMillis() - startTime
        val totalCombinations = calculateTotalCombinations(firstOptions, secondOptions, thirdOptions)
        
        log.info("Successfully crawled Ably product goodsId: {} in {}ms, options: {}/{}/{} (total combinations: {})", 
                goodsId, duration, firstOptions.size, secondOptions.size, thirdOptions.size, totalCombinations)
    }

    private fun calculateTotalCombinations(firstOptions: List<String>, secondOptions: List<String>, thirdOptions: List<String>): Int {
        return when {
            firstOptions.isEmpty() -> 0
            secondOptions.isEmpty() && thirdOptions.isEmpty() -> firstOptions.size
            secondOptions.isEmpty() -> firstOptions.size * thirdOptions.size
            thirdOptions.isEmpty() -> firstOptions.size * secondOptions.size
            else -> firstOptions.size * secondOptions.size * thirdOptions.size
        }
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

    private fun fetchOptionsData(goodsId: Long): AblyOptionsResponse? {
        return try {
            val optionsUrl = OPTIONS_API_URL_TEMPLATE.format(goodsId, FIRST_LEVEL_DEPTH)
            log.debug("Fetching options data from API: {}", optionsUrl)
            
            val response = makeApiRequest(optionsUrl, ANONYMOUS_TOKEN, AblyOptionsResponse::class.java)
            
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
    private fun extractAllOptionsRecursively(goodsId: Long, optionsData: AblyOptionsResponse?): Triple<List<String>, List<String>, List<String>> {
        if (optionsData?.optionComponents == null) {
            log.debug("No option components found, returning empty options")
            return Triple(emptyList(), emptyList(), emptyList())
        }
        
        val firstOptions = mutableSetOf<String>()
        val secondOptions = mutableSetOf<String>()
        val thirdOptions = mutableSetOf<String>()
        
        val context = OptionProcessingContext(goodsId, firstOptions, secondOptions, thirdOptions)
        
        // Process each first-level option component
        optionsData.optionComponents.forEach { component ->
            processFirstLevelOption(component, context)
        }
        
        log.debug("Recursively extracted options - first: {}, second: {}, third: {}", 
                 firstOptions.size, secondOptions.size, thirdOptions.size)
        
        return Triple(firstOptions.toList(), secondOptions.toList(), thirdOptions.toList())
    }
    
    private fun processFirstLevelOption(component: AblyOptionComponent, context: OptionProcessingContext) {
        val optionName = extractOptionName(component.name)
        context.firstOptions.add(optionName)
        
        if (component.isFinalDepth) return
        
        val nestedOptions = fetchNestedOptionsQuick(context.goodsId, component.goodsOptionSno, SECOND_LEVEL_DEPTH) ?: return
        nestedOptions.optionComponents?.forEach { nestedComponent ->
            processSecondLevelOption(nestedComponent, context)
        }
    }
    
    private fun processSecondLevelOption(nestedComponent: AblyOptionComponent, context: OptionProcessingContext) {
        val nestedOptionName = extractOptionName(nestedComponent.name)
        
        when (nestedComponent.depth) {
            SECOND_LEVEL_DEPTH -> context.secondOptions.add(nestedOptionName)
            THIRD_LEVEL_DEPTH -> context.thirdOptions.add(nestedOptionName)
            else -> context.secondOptions.add(nestedOptionName)
        }
        
        if (!nestedComponent.isFinalDepth) {
            val thirdLevelOptions = fetchNestedOptionsQuick(context.goodsId, nestedComponent.goodsOptionSno, THIRD_LEVEL_DEPTH) ?: return
            thirdLevelOptions.optionComponents?.forEach { thirdComponent ->
                val thirdOptionName = extractOptionName(thirdComponent.name)
                context.thirdOptions.add(thirdOptionName)
            }
        }
    }
    
    /**
     * 특정 옵션이 선택된 상태에서 하위 옵션들을 빠르게 가져옴 (타임아웃 적용)
     */
    private fun fetchNestedOptionsQuick(goodsId: Long, selectedOptionSno: Long, depth: Int = 1): AblyOptionsResponse? {
        return try {
            val nestedOptionsUrl = OPTIONS_WITH_SELECTION_URL_TEMPLATE.format(goodsId, depth, selectedOptionSno)
            
            val response = makeApiRequest(nestedOptionsUrl, ANONYMOUS_TOKEN, AblyOptionsResponse::class.java)
            
            response
        } catch (_: Exception) {
            null
        }
    }
    
    /**
     * 공통 HTTP API 요청 메서드 - Cloudflare 우회를 위한 향상된 헤더 설정
     */
    private fun <T> makeApiRequest(url: String, token: String, responseType: Class<T>, retryCount: Int = 0): T? {
        return try {
            val userAgent = USER_AGENTS[retryCount % USER_AGENTS.size]
            val response = restClient.get()
                .uri(url)
                .header("x-anonymous-token", token)
                .header("User-Agent", userAgent)
                .header("Accept", "application/json, text/plain, */*")
                .header("Accept-Language", "ko-KR,ko;q=0.9,en;q=0.8")
                .header("Accept-Encoding", "gzip, deflate, br")
                .header("Connection", "keep-alive")
                .header("Sec-Fetch-Dest", "empty")
                .header("Sec-Fetch-Mode", "cors")
                .header("Sec-Fetch-Site", "same-site")
                .header("Sec-Ch-Ua", "\"Not_A Brand\";v=\"8\", \"Chromium\";v=\"120\", \"Google Chrome\";v=\"120\"")
                .header("Sec-Ch-Ua-Mobile", "?0")
                .header("Sec-Ch-Ua-Platform", "\"Linux\"")
                .header("Cache-Control", "no-cache")
                .header("Pragma", "no-cache")
                .header("Origin", "https://m.a-bly.com")
                .header("Referer", "https://m.a-bly.com/")
                .retrieve()
                .body(responseType)
            
            log.debug("API request successful with User-Agent: {}", userAgent)
            response
        } catch (e: Exception) {
            if (retryCount < MAX_RETRY_COUNT - 1) {
                log.warn("API request failed (attempt {}), retrying with different User-Agent: {}", retryCount + 1, e.message)
                Thread.sleep(RETRY_DELAY_MS + (retryCount * 1000L))
                return makeApiRequest(url, token, responseType, retryCount + 1)
            }
            
            log.debug("API request failed after {} attempts for URL: {}", MAX_RETRY_COUNT, url)
            null
        }
    }
    
    /**
     * 403 에러 시 토큰 갱신 후 재시도 로직
     */
    private fun fetchWithRetry(apiUrl: String, goodsId: Long): AblyApiResponse {
        return try {
            makeApiRequest(apiUrl, ANONYMOUS_TOKEN, AblyApiResponse::class.java)
                ?: throw BusinessException(ErrorCode.CRAWLER_INVALID_RESPONSE)
        } catch (e: Exception) {
            when {
                e.message?.contains("403") == true -> {
                    log.warn("Authentication failed (403 Forbidden) for goodsId: {}. Trying token refresh and retry...", goodsId)
                    
                    try {
                        val newToken = ANONYMOUS_TOKEN
                        log.info("Token refreshed for goodsId: {}, retrying with new token", goodsId)
                        Thread.sleep(RETRY_DELAY_MS)
                        
                        makeApiRequest(apiUrl, newToken, AblyApiResponse::class.java)
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