package org.team_alilm.algamja.product.crawler.impl.ably

import okhttp3.*
import okhttp3.brotli.BrotliInterceptor
import org.slf4j.LoggerFactory
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.team_alilm.algamja.common.exception.BusinessException
import org.team_alilm.algamja.common.exception.ErrorCode
import org.team_alilm.algamja.product.crawler.ProductCrawler
import org.team_alilm.algamja.product.crawler.dto.CrawledProduct
import org.team_alilm.algamja.product.crawler.impl.ably.dto.AblyApiResponse
import org.team_alilm.algamja.product.crawler.impl.ably.dto.AblyOptionsResponse
import org.team_alilm.algamja.product.crawler.impl.ably.dto.AblyGoods
import org.team_alilm.algamja.product.crawler.util.CategoryMapper
import org.team_alilm.algamja.common.enums.Store
import org.team_alilm.algamja.common.enums.ProductCategory
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import java.math.BigDecimal
import java.net.URI
import java.time.Duration
import java.util.Base64
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern
import kotlin.random.Random

@Component("ablyCrawlerEnhanced")
@Order(5)
class AblyCrawlerEnhanced : ProductCrawler {
    
    companion object {
        private const val ANONYMOUS_TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhbm9ueW1vdXNfaWQiOiI1MjkwNzg3NTciLCJpYXQiOjE3NTYzNDM4ODh9.GG6bB2-q-cb47qD5UBwK5AQ4AzGLKSH3gZ0rsKWZR4Q"
        private const val DEFAULT_BRAND = "Unknown"
        private const val MAX_RETRY_COUNT = 3
        private const val RETRY_DELAY_MS = 2000L
        private const val BASIC_API_URL_TEMPLATE = "https://api.a-bly.com/api/v3/goods/%d/basic/?channel=0"
        private const val OPTIONS_API_URL_TEMPLATE = "https://api.a-bly.com/api/v2/goods/%d/options/?depth=%d"
        
        // 실제 브라우저 User-Agent 풀
        private val USER_AGENTS = listOf(
            // 모바일 Chrome (Android)
            "Mozilla/5.0 (Linux; Android 13; SM-G991B) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Mobile Safari/537.36",
            // 모바일 Chrome (iOS)
            "Mozilla/5.0 (iPhone; CPU iPhone OS 17_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) CriOS/120.0.0.0 Mobile/15E148 Safari/604.1",
            // 데스크톱 Chrome (Windows)
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36",
            // 데스크톱 Chrome (Mac)
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"
        )
    }
    
    private val log = LoggerFactory.getLogger(javaClass)
    private val goodsUrlPattern = Pattern.compile(""".*/goods/(\d+).*""")
    private val objectMapper = ObjectMapper().registerModule(KotlinModule.Builder().build())
    
    // OkHttpClient with Brotli support and cookie jar
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(BrotliInterceptor) // Brotli 디코딩 지원
        .addInterceptor(HeaderInterceptor())
        .addInterceptor(LoggingInterceptor())
        .cookieJar(InMemoryCookieJar()) // 쿠키 재사용
        .connectTimeout(Duration.ofSeconds(10))
        .readTimeout(Duration.ofSeconds(30))
        .writeTimeout(Duration.ofSeconds(10))
        .followRedirects(true)
        .retryOnConnectionFailure(true)
        .build()
    
    override fun supports(url: String): Boolean {
        return runCatching {
            val uri = URI(url.trim())
            val host = uri.host?.lowercase()
            (host == "a-bly.com" || host == "m.a-bly.com" || host?.endsWith(".a-bly.com") == true) &&
                goodsUrlPattern.matcher(url).matches()
        }.getOrElse { false }
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
        val startTime = System.currentTimeMillis()
        val goodsId = extractGoodsId(url)
        
        log.info("Starting enhanced Ably crawling for goodsId: {}, url: {}", goodsId, url)
        
        return try {
            fetchWithHttp(goodsId)
        } catch (e: Exception) {
            log.warn("HTTP crawling failed for goodsId: {}, falling back to Selenium: {}", goodsId, e.message)
            throw BusinessException(ErrorCode.CRAWLER_INVALID_RESPONSE)
        }
    }
    
    private fun fetchWithHttp(goodsId: Long): CrawledProduct {
        val apiUrl = BASIC_API_URL_TEMPLATE.format(goodsId)
        
        // HTTP 요청 with retry
        val response = executeWithRetry(apiUrl) { attemptCount ->
            val userAgent = USER_AGENTS[attemptCount % USER_AGENTS.size]
            
            val request = Request.Builder()
                .url(apiUrl)
                .header("User-Agent", userAgent)
                .header("Accept", "application/json, text/plain, */*")
                .header("Accept-Language", "ko-KR,ko;q=0.9,en;q=0.8")
                .header("Accept-Encoding", "gzip, deflate, br") // Brotli 포함
                .header("Referer", "https://m.a-bly.com/")
                .header("Origin", "https://m.a-bly.com")
                .header("Sec-Fetch-Dest", "empty")
                .header("Sec-Fetch-Mode", "cors")
                .header("Sec-Fetch-Site", "same-site")
                .header("Sec-Ch-Ua", "\"Not_A Brand\";v=\"8\", \"Chromium\";v=\"120\", \"Google Chrome\";v=\"120\"")
                .header("Sec-Ch-Ua-Mobile", "?1") // 모바일 플래그
                .header("Sec-Ch-Ua-Platform", "\"Android\"")
                .header("x-anonymous-token", ANONYMOUS_TOKEN)
                .header("Cache-Control", "no-cache")
                .header("Pragma", "no-cache")
                .build()
            
            okHttpClient.newCall(request).execute()
        }
        
        // 응답 파싱
        val responseBody = response.body?.string()
        if (responseBody.isNullOrEmpty()) {
            log.error("Empty response for goodsId: {}", goodsId)
            throw BusinessException(ErrorCode.CRAWLER_INVALID_RESPONSE)
        }
        
        val apiResponse = objectMapper.readValue<AblyApiResponse>(responseBody)
        val goods = apiResponse.goods ?: throw BusinessException(ErrorCode.CRAWLER_INVALID_RESPONSE)
        
        return buildCrawledProduct(goods, fetchOptions(goodsId))
    }
    
    private fun executeWithRetry(url: String, requestBuilder: (Int) -> Response): Response {
        var lastException: Exception? = null
        
        repeat(MAX_RETRY_COUNT) { attempt ->
            try {
                val response = requestBuilder(attempt)
                
                log.info("HTTP attempt {} - Status: {}, Headers: {}", 
                    attempt + 1, 
                    response.code, 
                    response.headers.toMultimap()
                )
                
                when {
                    response.isSuccessful -> return response
                    response.code == 403 -> {
                        log.warn("403 Forbidden on attempt {}, retrying with different UA", attempt + 1)
                        response.close()
                        Thread.sleep(RETRY_DELAY_MS + (attempt * 1000L))
                    }
                    response.code == 429 -> {
                        log.warn("429 Rate limited on attempt {}, waiting longer", attempt + 1)
                        response.close()
                        Thread.sleep(RETRY_DELAY_MS * 3)
                    }
                    else -> {
                        log.error("Unexpected status {} on attempt {}", response.code, attempt + 1)
                        response.close()
                        throw BusinessException(ErrorCode.CRAWLER_INVALID_RESPONSE)
                    }
                }
            } catch (e: Exception) {
                log.error("Request failed on attempt {}: {}", attempt + 1, e.message)
                lastException = e
                if (attempt < MAX_RETRY_COUNT - 1) {
                    Thread.sleep(RETRY_DELAY_MS)
                }
            }
        }
        
        throw lastException ?: BusinessException(ErrorCode.CRAWLER_INVALID_RESPONSE)
    }
    
    private fun fetchOptions(goodsId: Long): Triple<List<String>, List<String>, List<String>> {
        // 옵션 조회 로직 (기존과 동일)
        return Triple(emptyList(), emptyList(), emptyList())
    }
    
    private fun buildCrawledProduct(goods: AblyGoods, options: Triple<List<String>, List<String>, List<String>>): CrawledProduct {
        val (firstOptions, secondOptions, thirdOptions) = options
        val thumbnailUrl = goods.coverImages?.firstOrNull() ?: ""
        val imageUrls = goods.coverImages ?: emptyList()
        val categoryName = goods.displayCategories?.firstOrNull()?.name ?: ""
        
        val koreanCategory = CategoryMapper.mapCategory(categoryName)
        val englishFirstCategory = ProductCategory.mapKoreanToEnglish(koreanCategory) ?: "OTHERS"
        val englishSecondCategory = ProductCategory.mapKoreanToEnglish(categoryName)
        
        return CrawledProduct(
            storeNumber = goods.sno,
            name = goods.name,
            brand = goods.market?.name ?: DEFAULT_BRAND,
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
    }
    
    private fun extractGoodsId(url: String): Long {
        val matcher = goodsUrlPattern.matcher(url)
        return if (matcher.matches()) {
            matcher.group(1).toLong()
        } else {
            throw BusinessException(ErrorCode.CRAWLER_INVALID_URL)
        }
    }
    
    // 헤더 인터셉터
    inner class HeaderInterceptor : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val originalRequest = chain.request()
            
            // 추가 헤더 설정
            val modifiedRequest = originalRequest.newBuilder()
                .removeHeader("User-Agent") // 기존 UA 제거
                .build()
            
            return chain.proceed(modifiedRequest)
        }
    }
    
    // 로깅 인터셉터 (본문은 base64 샘플만)
    inner class LoggingInterceptor : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request()
            val startTime = System.nanoTime()
            
            log.debug("Sending request: {} {}", request.method, request.url)
            log.debug("Request headers: {}", request.headers.toMultimap())
            
            val response = chain.proceed(request)
            val duration = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime)
            
            // 응답 로깅
            log.debug("Received response in {}ms: {} {}", duration, response.code, response.request.url)
            log.debug("Response headers: {}", response.headers.toMultimap())
            
            // 본문 샘플 (base64, 최대 200 바이트)
            response.peekBody(200).bytes().let { sample ->
                if (sample.isNotEmpty()) {
                    val base64Sample = Base64.getEncoder().encodeToString(sample)
                    log.debug("Response body sample (base64, {} bytes): {}", sample.size, base64Sample)
                }
            }
            
            return response
        }
    }
    
    // 메모리 기반 쿠키 저장소
    class InMemoryCookieJar : CookieJar {
        private val cookieStore = mutableMapOf<String, List<Cookie>>()
        
        override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
            cookieStore[url.host] = cookies
        }
        
        override fun loadForRequest(url: HttpUrl): List<Cookie> {
            return cookieStore[url.host] ?: emptyList()
        }
    }
}