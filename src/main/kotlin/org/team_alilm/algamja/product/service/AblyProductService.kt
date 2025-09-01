package org.team_alilm.algamja.product.service

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.client.RestClient
import org.team_alilm.algamja.common.exception.BusinessException
import org.team_alilm.algamja.common.exception.ErrorCode
import org.team_alilm.algamja.product.crawler.CrawlerRegistry
import org.team_alilm.algamja.product.crawler.dto.CrawledProduct
import org.team_alilm.algamja.product.repository.ProductExposedRepository
import org.team_alilm.algamja.product.image.repository.ProductImageExposedRepository
import kotlin.random.Random

@Service
@Transactional
class AblyProductService(
    private val restClient: RestClient,
    private val crawlerRegistry: CrawlerRegistry,
    private val productExposedRepository: ProductExposedRepository,
    private val productImageExposedRepository: ProductImageExposedRepository
) {

    private val log = LoggerFactory.getLogger(javaClass)

    fun fetchAndRegisterRandomProducts(count: Int = 100): Int {
        log.info("Starting to fetch and register {} random Ably products", count)
        
        try {
            val productUrls = fetchRandomAblyProductUrls(count)
            log.info("Found {} Ably product URLs", productUrls.size)
            
            var successCount = 0
            var failCount = 0
            
            productUrls.forEach { url ->
                try {
                    val crawledProduct = crawlProductFromUrl(url)
                    if (crawledProduct != null) {
                        registerProduct(crawledProduct, url)
                        successCount++
                        log.debug("Successfully registered product from URL: {}", url)
                    } else {
                        failCount++
                        log.warn("Failed to crawl product from URL: {}", url)
                    }
                } catch (e: Exception) {
                    failCount++
                    log.error("Error processing product URL: {}", url, e)
                }
            }
            
            log.info("Product registration completed. Success: {}, Failed: {}", successCount, failCount)
            return successCount
            
        } catch (e: Exception) {
            log.error("Failed to fetch and register Ably products", e)
            throw BusinessException(ErrorCode.INTERNAL_ERROR, cause = e)
        }
    }

    private fun fetchRandomAblyProductUrls(count: Int): List<String> {
        val productUrls = mutableListOf<String>()
        
        try {
            // 1차: 랭킹 페이지에서 인기 상품들 가져오기
            val rankingUrls = fetchProductUrlsFromRanking(count)
            productUrls.addAll(rankingUrls)
            log.info("Fetched {} products from Ably ranking pages", rankingUrls.size)
            
            // 2차: 부족하면 인기 카테고리에서 추가로 가져오기
            if (productUrls.size < count) {
                val remaining = count - productUrls.size
                val categoryUrls = fetchProductUrlsFromCategories(remaining)
                productUrls.addAll(categoryUrls)
                log.info("Fetched {} additional products from Ably categories", categoryUrls.size)
            }
            
            // 랜덤하게 섞고 요청된 개수만큼 선택
            return productUrls.shuffled().take(count)
            
        } catch (e: Exception) {
            log.error("Failed to fetch product URLs from Ably ranking/categories", e)
            
            // 최종 대안: 랜덤 상품 ID 생성으로 URL 만들기
            log.info("Falling back to random product ID generation for Ably")
            return generateRandomProductUrls(count)
        }
    }

    private fun fetchProductUrlsFromRanking(count: Int): List<String> {
        val productUrls = mutableListOf<String>()
        
        // 에이블리 랭킹 페이지들
        val rankingPages = listOf(
            "https://a-bly.com/goods/best", // 베스트 상품
            "https://a-bly.com/goods/best?category_no=24", // 상의
            "https://a-bly.com/goods/best?category_no=25", // 아우터
            "https://a-bly.com/goods/best?category_no=26", // 원피스
            "https://a-bly.com/goods/best?category_no=27", // 하의
            "https://a-bly.com/goods/best?category_no=28", // 신발
            "https://a-bly.com/goods/weekly_best" // 주간 베스트
        )
        
        rankingPages.forEach { rankingUrl ->
            try {
                val urls = fetchProductUrlsFromPage(rankingUrl, count / rankingPages.size + 10)
                productUrls.addAll(urls)
                log.debug("Extracted {} URLs from Ably ranking page: {}", urls.size, rankingUrl)
            } catch (e: Exception) {
                log.warn("Failed to fetch from Ably ranking page {}: {}", rankingUrl, e.message)
            }
        }
        
        return productUrls.distinct()
    }

    private fun fetchProductUrlsFromCategories(count: Int): List<String> {
        val productUrls = mutableListOf<String>()
        
        // 에이블리 인기 카테고리들
        val categories = listOf(
            "24", // 상의
            "25", // 아우터
            "26", // 원피스
            "27", // 하의
            "28", // 신발
            "29", // 가방
            "30", // 악세서리
            "31", // 속옷
            "32"  // 수영복
        )
        
        categories.forEach { categoryId ->
            val urls = fetchProductUrlsFromCategory(categoryId, count / categories.size + 5)
            productUrls.addAll(urls)
        }
        
        return productUrls.distinct()
    }

    private fun fetchProductUrlsFromPage(pageUrl: String, limit: Int): List<String> {
        val productUrls = mutableListOf<String>()
        
        try {
            log.debug("Fetching products from Ably page URL: {}", pageUrl)
            
            val response = restClient.get()
                .uri(pageUrl)
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                .retrieve()
                .body(String::class.java)
                ?: throw BusinessException(ErrorCode.ABLY_INVALID_RESPONSE)
            
            // HTML에서 상품 URL 패턴 추출: href="/goods/[숫자]"
            val productUrlPattern = Regex("""href=['"]/goods/(\d+)['"]""")
            val matches = productUrlPattern.findAll(response)
            
            matches.take(limit).forEach { match ->
                val goodsId = match.groupValues[1]
                val productUrl = "https://a-bly.com/goods/$goodsId"
                productUrls.add(productUrl)
            }
            
            log.debug("Extracted {} product URLs from Ably page", productUrls.size)
            
        } catch (e: Exception) {
            log.warn("Failed to fetch products from Ably page {}: {}", pageUrl, e.message)
        }
        
        return productUrls
    }

    private fun fetchProductUrlsFromCategory(categoryId: String, limit: Int): List<String> {
        try {
            // 에이블리 카테고리 페이지 URL 생성 (인기순 정렬)
            val page = Random.nextInt(1, 11) // 1-10 페이지 중 랜덤 선택
            val categoryUrl = "https://a-bly.com/goods/list?category_no=$categoryId&sort=hit&page=$page"
            
            log.debug("Fetching products from Ably category URL: {}", categoryUrl)
            return fetchProductUrlsFromPage(categoryUrl, limit)
            
        } catch (e: Exception) {
            log.warn("Failed to fetch products from Ably category {}: {}", categoryId, e.message)
            return emptyList()
        }
    }

    private fun generateRandomProductUrls(count: Int): List<String> {
        val productUrls = mutableListOf<String>()
        val usedIds = mutableSetOf<Int>()
        
        // 에이블리 상품 ID 범위 (대략적인 범위)
        val minId = 100000
        val maxId = 1000000
        
        while (productUrls.size < count && usedIds.size < count * 2) {
            val randomId = Random.nextInt(minId, maxId)
            if (usedIds.add(randomId)) {
                productUrls.add("https://a-bly.com/goods/$randomId")
            }
        }
        
        log.debug("Generated {} random Ably product URLs", productUrls.size)
        return productUrls
    }

    private fun crawlProductFromUrl(url: String): CrawledProduct? {
        return try {
            val crawler = crawlerRegistry.resolve(url)
            val normalizedUrl = crawler.normalize(url)
            crawler.fetch(normalizedUrl)
        } catch (e: Exception) {
            log.debug("Failed to crawl product from URL: {} - {}", url, e.message)
            null
        }
    }

    private fun registerProduct(crawledProduct: CrawledProduct, originalUrl: String) {
        try {
            // 이미 존재하는 상품인지 확인 (storeNumber 기준)
            val existingProduct = productExposedRepository.fetchProductByStoreNumber(
                storeNumber = crawledProduct.storeNumber,
                store = crawledProduct.store
            )
            
            if (existingProduct != null) {
                log.debug("Product already exists: {}", crawledProduct.storeNumber)
                return
            }
            
            // 상품 등록
            val savedProduct = productExposedRepository.save(
                name = crawledProduct.name,
                storeNumber = crawledProduct.storeNumber,
                brand = crawledProduct.brand,
                thumbnailUrl = crawledProduct.thumbnailUrl,
                originalUrl = originalUrl,
                store = crawledProduct.store,
                price = crawledProduct.price,
                firstCategory = crawledProduct.firstCategory,
                secondCategory = crawledProduct.secondCategory,
                firstOptions = crawledProduct.firstOptions,
                secondOptions = crawledProduct.secondOptions,
                thirdOptions = crawledProduct.thirdOptions
            )
            
            // 상품 이미지 등록
            crawledProduct.imageUrls.forEachIndexed { index, imageUrl ->
                productImageExposedRepository.save(
                    productId = savedProduct.id,
                    imageUrl = imageUrl,
                    imageOrder = index
                )
            }
            
            log.debug("Successfully registered Ably product: {} (ID: {})", crawledProduct.name, savedProduct.id)
            
        } catch (e: Exception) {
            log.error("Failed to register Ably product: {}", crawledProduct.name, e)
            throw BusinessException(ErrorCode.INTERNAL_ERROR, cause = e)
        }
    }
}