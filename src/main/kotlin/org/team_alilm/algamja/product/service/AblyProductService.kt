package org.team_alilm.algamja.product.service

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.client.RestClient
import org.team_alilm.algamja.common.exception.BusinessException
import org.team_alilm.algamja.common.exception.ErrorCode
import org.team_alilm.algamja.product.crawler.CrawlerRegistry
import org.team_alilm.algamja.product.crawler.dto.CrawledProduct
import org.team_alilm.algamja.product.crawler.impl.ably.AblyTokenManager
import org.team_alilm.algamja.product.dto.AblyTodayResponse
import org.team_alilm.algamja.product.repository.ProductExposedRepository
import org.team_alilm.algamja.product.image.repository.ProductImageExposedRepository
import kotlin.random.Random

@Service
@Transactional
class AblyProductService(
    private val restClient: RestClient,
    private val crawlerRegistry: CrawlerRegistry,
    private val productExposedRepository: ProductExposedRepository,
    private val productImageExposedRepository: ProductImageExposedRepository,
    private val ablyTokenManager: AblyTokenManager,
    private val objectMapper: ObjectMapper
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
                log.info("Ably product already exists, skipping: {} (storeNumber: {})", crawledProduct.name, crawledProduct.storeNumber)
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
            
            // 상품 이미지 등록 (중복 체크)
            var newImageCount = 0
            crawledProduct.imageUrls.forEachIndexed { index, imageUrl ->
                val savedImage = productImageExposedRepository.saveIfNotExists(
                    productId = savedProduct.id,
                    imageUrl = imageUrl,
                    imageOrder = index
                )
                if (savedImage != null) {
                    newImageCount++
                }
            }
            
            if (newImageCount > 0) {
                log.debug("Added {} new images for product: {}", newImageCount, crawledProduct.name)
            }
            
            log.debug("Successfully registered Ably product: {} (ID: {})", crawledProduct.name, savedProduct.id)
            
        } catch (e: Exception) {
            log.error("Failed to register Ably product: {}", crawledProduct.name, e)
            throw BusinessException(ErrorCode.INTERNAL_ERROR, cause = e)
        }
    }
    
    /**
     * 에이블리 랭킹 페이지에서 상품을 가져와 등록하는 메서드
     * 랭킹 페이지를 우선적으로 사용하고, 실패 시 기존 방식으로 fallback
     */
    fun fetchAndRegisterRankingProducts(count: Int = 100): Int {
        log.info("Starting to fetch and register {} products from Ably ranking pages", count)
        
        try {
            // 랭킹 페이지에서 우선적으로 상품 URL 가져오기
            val rankingUrls = fetchProductUrlsFromRanking(count)
            
            if (rankingUrls.isEmpty()) {
                log.warn("No products found from Ably ranking pages, falling back to random products")
                return fetchAndRegisterRandomProducts(count)
            }
            
            log.info("Found {} products from Ably ranking pages", rankingUrls.size)
            
            var successCount = 0
            var failCount = 0
            
            rankingUrls.forEach { url ->
                try {
                    val crawledProduct = crawlProductFromUrl(url)
                    if (crawledProduct != null) {
                        // 이미 존재하는 상품인지 확인
                        val existingProduct = productExposedRepository.fetchProductByStoreNumber(
                            storeNumber = crawledProduct.storeNumber,
                            store = crawledProduct.store
                        )
                        
                        if (existingProduct != null) {
                            log.info("Ably ranking product already exists, skipping: {} (storeNumber: {})", 
                                crawledProduct.name, crawledProduct.storeNumber)
                            return@forEach  // 다음 상품으로 넘어감
                        }
                        
                        registerProduct(crawledProduct, url)
                        successCount++
                        log.debug("Successfully registered Ably ranking product from URL: {}", url)
                    } else {
                        failCount++
                        log.warn("Failed to crawl Ably ranking product from URL: {}", url)
                    }
                } catch (e: Exception) {
                    failCount++
                    log.error("Error processing Ably ranking product URL: {}", url, e)
                }
            }
            
            // 부족한 경우 추가로 가져오기
            if (successCount < count / 2) {
                log.info("Only {} products registered from ranking, fetching additional products", successCount)
                val additionalCount = fetchAndRegisterRandomProducts(count - successCount)
                return successCount + additionalCount
            }
            
            log.info("Ably ranking product registration completed. Success: {}, Failed: {}", successCount, failCount)
            return successCount
            
        } catch (e: Exception) {
            log.error("Failed to fetch products from Ably ranking pages, falling back to random", e)
            return fetchAndRegisterRandomProducts(count)
        }
    }
    
    /**
     * 모든 등록된 에이블리 상품의 가격을 업데이트하는 메서드
     */
    fun updateAllProductPrices(): Int {
        log.info("Starting price update for all existing Ably products")
        
        try {
            val batchSize = 50
            var offset = 0
            var totalUpdatedCount = 0
            
            while (true) {
                // 배치 단위로 에이블리 상품 조회
                val productBatch = productExposedRepository.fetchAblyProductsForPriceUpdateBatch(batchSize, offset)
                
                if (productBatch.isEmpty()) {
                    break
                }
                
                log.info("Processing Ably batch: {} products (offset: {})", productBatch.size, offset)
                
                var batchUpdatedCount = 0
                
                productBatch.forEach { product ->
                    try {
                        val productUrl = "https://a-bly.com/goods/${product.storeNumber}"
                        val crawledProduct = crawlProductFromUrl(productUrl)
                        
                        if (crawledProduct != null) {
                            val oldPrice = product.price
                            val newPrice = crawledProduct.price
                            
                            if (oldPrice != newPrice) {
                                productExposedRepository.updatePrice(product.id, newPrice)
                                log.debug("Price updated for Ably product {}: {} -> {}", 
                                    product.name, oldPrice, newPrice)
                                batchUpdatedCount++
                            }
                        }
                        
                        Thread.sleep(100) // CPU 부하 방지
                        
                    } catch (e: Exception) {
                        log.warn("Failed to update price for Ably product {}: {}", product.name, e.message)
                    }
                }
                
                totalUpdatedCount += batchUpdatedCount
                offset += batchSize
                
                log.info("Ably batch completed: {} products updated", batchUpdatedCount)
                Thread.sleep(500) // 배치 간 휴식
            }
            
            log.info("All Ably products price update completed: {} products updated", totalUpdatedCount)
            return totalUpdatedCount
            
        } catch (e: Exception) {
            log.error("Failed to update all Ably product prices", e)
            return 0
        }
    }
    
    /**
     * 에이블리 TODAY API에서 상품을 가져와 등록하는 메서드
     * https://api.a-bly.com/api/v2/screens/TODAY/
     * 익명 토큰을 사용하여 API 호출
     * nextToken을 활용한 페이지네이션으로 최대 100개까지 상품 수집
     */
    fun fetchAndRegisterTodayProducts(count: Int = 100): Int {
        log.info("Starting to fetch and register {} products from Ably TODAY API", count)
        
        try {
            // 1. 익명 토큰 획득
            val token = ablyTokenManager.getToken()
            
            // 2. TODAY API에서 상품 SNO 수집
            val allProductSnos = fetchTodayProductSnos(token, count)
            
            if (allProductSnos.isEmpty()) {
                log.warn("No products found from Ably TODAY API, falling back to ranking")
                return fetchAndRegisterRankingProducts(count)
            }
            
            // 3. 중복 제거 및 제한
            val uniqueSnos = allProductSnos.distinct().take(count)
            log.info("Processing {} unique products (limit: {})", uniqueSnos.size, count)
            
            // 4. 각 상품 처리
            val result = processTodayProducts(uniqueSnos)
            
            log.info("Ably TODAY product registration completed. Success: {}, Failed: {}", 
                result.first, result.second)
            
            // 부족한 경우 또는 403 에러가 많이 발생한 경우 랭킹에서 가져오기
            if (result.first < count / 3) { // 임계값을 더 낮게 조정 (1/2 -> 1/3)
                log.warn("Too few products registered from TODAY API ({}), likely due to authentication issues. Fetching from ranking instead.", result.first)
                val additionalCount = fetchAndRegisterRankingProducts(count - result.first)
                return result.first + additionalCount
            }
            
            return result.first
            
        } catch (e: Exception) {
            log.error("Failed to fetch products from Ably TODAY API, falling back to ranking", e)
            return fetchAndRegisterRankingProducts(count)
        }
    }
    
    /**
     * TODAY API에서 상품 SNO 목록을 수집하는 메서드
     */
    private fun fetchTodayProductSnos(token: String, count: Int): List<Long> {
        val allProductSnos = mutableListOf<Long>()
        var nextToken: String? = null
        var pageCount = 0
        val maxPages = 5 // 최대 5페이지까지만 조회 (경험적으로 첫 페이지 이후엔 상품이 거의 없음)
        var consecutiveEmptyPages = 0
        
        while (allProductSnos.size < count && pageCount < maxPages) {
            val apiUrl = buildTodayApiUrl(nextToken)
            log.info("Fetching TODAY API page {}: {}", pageCount + 1, apiUrl)
            
            val response = fetchTodayApiResponse(apiUrl, token) ?: break
            val todayResponse = objectMapper.readValue(response, AblyTodayResponse::class.java)
            
            val pageProductSnos = extractProductSnosFromResponse(todayResponse)
            
            if (pageProductSnos.isEmpty()) {
                consecutiveEmptyPages++
                log.info("No products found on page {}, consecutive empty pages: {}", pageCount + 1, consecutiveEmptyPages)
                
                // 연속 3페이지 빈 페이지면 조기 종료 (최적화)
                if (consecutiveEmptyPages >= 3) {
                    log.info("3 consecutive empty pages found, stopping pagination early")
                    break
                }
            } else {
                consecutiveEmptyPages = 0 // 상품을 찾으면 카운터 리셋
                allProductSnos.addAll(pageProductSnos)
                log.info("Found {} products on page {}, total: {}", 
                    pageProductSnos.size, pageCount + 1, allProductSnos.size)
            }
            
            // nextToken 확인 및 로깅
            nextToken = todayResponse.nextToken
            if (nextToken != null) {
                log.info("Next token received: {}", nextToken)
            } else {
                log.info("No next token - this is the last page")
                break
            }
            
            pageCount++
            
            // 이미 충분한 상품을 수집했으면 중단
            if (allProductSnos.size >= count) {
                log.info("Collected enough products ({}), stopping pagination", allProductSnos.size)
                break
            }
        }
        
        log.info("Found total {} products from Ably TODAY API across {} pages", 
            allProductSnos.size, if (pageCount == 0 && allProductSnos.isNotEmpty()) 1 else pageCount)
        
        return allProductSnos
    }
    
    /**
     * TODAY API URL 생성
     */
    private fun buildTodayApiUrl(nextToken: String?): String {
        return if (nextToken == null) {
            "https://api.a-bly.com/api/v2/screens/TODAY/"
        } else {
            "https://api.a-bly.com/api/v2/screens/TODAY/?next_token=$nextToken"
        }
    }
    
    /**
     * TODAY API 호출
     */
    private fun fetchTodayApiResponse(apiUrl: String, token: String): String? {
        try {
            val response = restClient.get()
                .uri(apiUrl)
                .header("x-anonymous-token", token)
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                .header("Accept", "application/json")
                .retrieve()
                .body(String::class.java)
            
            return response
        } catch (e: Exception) {
            log.error("Failed to fetch from Ably TODAY API: {}", apiUrl, e)
            return null
        }
    }
    
    /**
     * API 응답에서 상품 SNO 추출
     */
    private fun extractProductSnosFromResponse(response: AblyTodayResponse): List<Long> {
        val productSnos = mutableListOf<Long>()
        
        response.components?.forEach { component ->
            val itemListType = component.type?.itemList
            log.debug("Component type: {}", itemListType)
            
            // 상품이 포함될 수 있는 모든 컴포넌트 타입 처리
            if (itemListType != null && (
                itemListType.contains("GOODS_LIST") || 
                itemListType.contains("GOODS") ||
                itemListType.contains("PRODUCT") ||
                itemListType.contains("ITEM") ||
                itemListType.contains("CARD_LIST") ||  // TWO_COL_CARD_LIST, THREE_COL_CARD_LIST 등
                itemListType.contains("LIST")  // 기타 리스트 형태
            )) {
                var componentProductCount = 0
                component.entity?.itemList?.forEach { wrapper ->
                    // 첫 번째 구조: wrapper.item
                    wrapper.item?.sno?.let { 
                        productSnos.add(it)
                        componentProductCount++
                    }
                    
                    // 두 번째 구조: wrapper.itemEntity.item (nextToken 사용 시)
                    wrapper.itemEntity?.item?.sno?.let {
                        productSnos.add(it)
                        componentProductCount++
                    }
                }
                log.debug("Component '{}': found {} products", itemListType, componentProductCount)
            }
        }
        
        return productSnos
    }
    
    /**
     * TODAY 상품들을 처리하는 메서드
     * @return Pair<successCount, failCount>
     */
    private fun processTodayProducts(uniqueSnos: List<Long>): Pair<Int, Int> {
        var successCount = 0
        var failCount = 0
        
        uniqueSnos.forEachIndexed { index, sno ->
            try {
                // Rate limiting: 너무 빠른 요청 방지 (첫 번째 요청 제외)
                if (index > 0) {
                    Thread.sleep(500) // 0.5초 지연
                }
                
                // 크롤링 및 등록 (이미 중복 체크가 포함됨)
                if (crawlAndRegisterProduct(sno)) {
                    successCount++
                } else {
                    failCount++
                }
            } catch (e: Exception) {
                failCount++
                log.error("Error processing Ably TODAY product sno={}: {}", sno, e.message)
            }
        }
        
        return Pair(successCount, failCount)
    }
    
    /**
     * 상품 존재 여부 확인
     */
    private fun isProductExists(sno: Long): Boolean {
        val existingProduct = productExposedRepository.fetchProductByStoreNumber(
            storeNumber = sno,
            store = org.team_alilm.algamja.common.enums.Store.ABLY
        )
        return existingProduct != null
    }
    
    /**
     * 상품 크롤링 및 등록
     */
    private fun crawlAndRegisterProduct(sno: Long): Boolean {
        // 먼저 중복 체크
        if (isProductExists(sno)) {
            log.info("Ably TODAY product already exists, skipping: sno={}", sno)
            return true  // 이미 존재하므로 성공으로 처리
        }
        
        val productUrl = "https://a-bly.com/goods/$sno"
        
        // 토큰 상태 디버깅
        val token = ablyTokenManager.getToken()
        log.debug("Using token for crawling sno={}: {}...", sno, token.take(20))
        
        val crawledProduct = crawlProductFromUrl(productUrl)
        
        return if (crawledProduct != null) {
            registerProduct(crawledProduct, productUrl)
            log.debug("Successfully registered Ably TODAY product: sno={}", sno)
            true
        } else {
            log.warn("Failed to crawl Ably TODAY product: sno={}", sno)
            false
        }
    }
}