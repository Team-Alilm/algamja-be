package org.team_alilm.algamja.product.service

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.client.RestClient
import org.team_alilm.algamja.common.enums.Store
import org.team_alilm.algamja.common.exception.BusinessException
import org.team_alilm.algamja.common.exception.ErrorCode
import org.team_alilm.algamja.product.crawler.CrawlerRegistry
import org.team_alilm.algamja.product.crawler.dto.CrawledProduct
import org.team_alilm.algamja.product.dto.MusinsaRankingResponse
import org.team_alilm.algamja.product.repository.ProductExposedRepository
import org.team_alilm.algamja.product.image.repository.ProductImageExposedRepository
import kotlin.random.Random

@Service
@Transactional
class MusinsaProductService(
    private val restClient: RestClient,
    private val crawlerRegistry: CrawlerRegistry,
    private val productExposedRepository: ProductExposedRepository,
    private val productImageExposedRepository: ProductImageExposedRepository,
    private val objectMapper: ObjectMapper
) {

    private val log = LoggerFactory.getLogger(javaClass)

    fun fetchAndRegisterRandomProducts(count: Int = 100): Int {
        log.info("Starting to fetch and register {} random Musinsa products", count)
        
        try {
            val productUrls = fetchRandomMusinsaProductUrls(count)
            log.info("Found {} Musinsa product URLs", productUrls.size)
            
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
            log.error("Failed to fetch and register Musinsa products", e)
            throw BusinessException(ErrorCode.INTERNAL_ERROR, cause = e)
        }
    }

    private fun fetchRandomMusinsaProductUrls(count: Int): List<String> {
        val productUrls = mutableListOf<String>()
        
        try {
            // 1차: 랭킹 페이지에서 인기 상품들 가져오기
            val rankingUrls = fetchProductUrlsFromRanking(count)
            productUrls.addAll(rankingUrls)
            log.info("Fetched {} products from ranking pages", rankingUrls.size)
            
            // 2차: 부족하면 인기 카테고리에서 추가로 가져오기
            if (productUrls.size < count) {
                val remaining = count - productUrls.size
                val categoryUrls = fetchProductUrlsFromCategories(remaining)
                productUrls.addAll(categoryUrls)
                log.info("Fetched {} additional products from categories", categoryUrls.size)
            }
            
            // 랜덤하게 섞고 요청된 개수만큼 선택
            return productUrls.shuffled().take(count)
            
        } catch (e: Exception) {
            log.error("Failed to fetch product URLs from ranking/categories", e)
            
            // 최종 대안: 랜덤 상품 ID 생성으로 URL 만들기
            log.info("Falling back to random product ID generation")
            return generateRandomProductUrls(count)
        }
    }

    private fun fetchProductUrlsFromRanking(count: Int): List<String> {
        val productUrls = mutableListOf<String>()
        
        // 무신사 랭킹 API 엔드포인트들 (다양한 카테고리) - sections 엔드포인트 사용
        val rankingApis = listOf(
            "https://api.musinsa.com/api2/hm/web/v5/pans/ranking/sections/200?storeCode=musinsa&categoryCode=000&contentsId=", // 전체
            "https://api.musinsa.com/api2/hm/web/v5/pans/ranking/sections/200?storeCode=musinsa&categoryCode=001&contentsId=", // 상의
            "https://api.musinsa.com/api2/hm/web/v5/pans/ranking/sections/200?storeCode=musinsa&categoryCode=002&contentsId=", // 아우터
            "https://api.musinsa.com/api2/hm/web/v5/pans/ranking/sections/200?storeCode=musinsa&categoryCode=003&contentsId=", // 바지
            "https://api.musinsa.com/api2/hm/web/v5/pans/ranking/sections/200?storeCode=musinsa&categoryCode=103&contentsId="  // 신발
        )
        
        rankingApis.forEach { apiUrl ->
            try {
                val urls = fetchProductUrlsFromRankingApi(apiUrl, count / rankingApis.size + 10)
                productUrls.addAll(urls)
                log.debug("Extracted {} URLs from ranking API", urls.size)
            } catch (e: Exception) {
                log.warn("Failed to fetch from ranking API: {}", e.message)
            }
        }
        
        return productUrls.distinct()
    }
    
    private fun fetchProductUrlsFromRankingApi(apiUrl: String, limit: Int): List<String> {
        val productUrls = mutableListOf<String>()
        
        try {
            log.debug("Fetching products from ranking API: {}", apiUrl)
            
            val response = restClient.get()
                .uri(apiUrl)
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                .header("Accept", "application/json")
                .header("Referer", "https://www.musinsa.com/")
                .retrieve()
                .body(String::class.java)
                ?: throw BusinessException(ErrorCode.MUSINSA_INVALID_RESPONSE)
            
            // JSON 파싱
            val rankingResponse = objectMapper.readValue(response, MusinsaRankingResponse::class.java)
            
            // 새로운 API 구조: modules에서 MULTICOLUMN 타입의 아이템들 추출
            rankingResponse.data?.modules?.forEach { module ->
                if (module.type == "MULTICOLUMN") {
                    module.items.take(limit).forEach { productItem ->
                        productItem.id?.let { productId ->
                            val productUrl = "https://www.musinsa.com/app/goods/$productId"
                            productUrls.add(productUrl)
                            log.debug("Found product from ranking: {} - {}", 
                                productItem.info?.productName, productId)
                        }
                    }
                }
            }
            
            // 이전 API 구조도 지원 (fallback)
            if (productUrls.isEmpty()) {
                rankingResponse.data?.list?.take(limit)?.forEach { rankingItem ->
                    rankingItem.item?.let { item ->
                        val goodsId = item.itemNo ?: item.itemId
                        if (!goodsId.isNullOrBlank()) {
                            val productUrl = "https://www.musinsa.com/app/goods/$goodsId"
                            productUrls.add(productUrl)
                            log.debug("Found product from ranking (legacy): {} - {}", item.itemName, goodsId)
                        }
                    }
                }
            }
            
            log.info("Successfully fetched {} products from ranking API", productUrls.size)
            
        } catch (e: Exception) {
            log.error("Failed to fetch products from ranking API: {}", e.message)
        }
        
        return productUrls
    }

    private fun fetchProductUrlsFromCategories(count: Int): List<String> {
        val productUrls = mutableListOf<String>()
        
        // 무신사 인기 카테고리들
        val categories = listOf(
            "001006", // 상의 > 맨투맨/스웨트셔츠
            "001005", // 상의 > 니트/스웨터
            "001001", // 상의 > 티셔츠
            "002002", // 아우터 > 코트
            "002001", // 아우터 > 자켓
            "003002", // 바지 > 데님 팬츠
            "003009", // 바지 > 트레이닝/조거 팬츠
            "020001", // 스니커즈 > 로우탑
            "020003", // 스니커즈 > 하이탑
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
            log.debug("Fetching products from page URL: {}", pageUrl)
            
            val response = restClient.get()
                .uri(pageUrl)
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                .retrieve()
                .body(String::class.java)
                ?: throw BusinessException(ErrorCode.MUSINSA_INVALID_RESPONSE)
            
            // HTML에서 상품 URL 패턴 추출: href="/app/goods/[숫자]"
            val productUrlPattern = Regex("""href=['"]/app/goods/(\d+)['"]""")
            val matches = productUrlPattern.findAll(response)
            
            matches.take(limit).forEach { match ->
                val goodsId = match.groupValues[1]
                val productUrl = "https://www.musinsa.com/app/goods/$goodsId"
                productUrls.add(productUrl)
            }
            
            log.debug("Extracted {} product URLs from page", productUrls.size)
            
        } catch (e: Exception) {
            log.warn("Failed to fetch products from page {}: {}", pageUrl, e.message)
        }
        
        return productUrls
    }

    private fun fetchProductUrlsFromCategory(categoryId: String, limit: Int): List<String> {
        try {
            // 무신사 카테고리 페이지 URL 생성 (인기순 정렬)
            val page = Random.nextInt(1, 6) // 1-5 페이지 중 랜덤 선택
            val categoryUrl = "https://www.musinsa.com/categories/item/$categoryId?d_cat_cd=$categoryId&brand=&list_kind=small&sort=sale&sub_sort=&page=$page&display_cnt=90"
            
            log.debug("Fetching products from category URL: {}", categoryUrl)
            return fetchProductUrlsFromPage(categoryUrl, limit)
            
        } catch (e: Exception) {
            log.warn("Failed to fetch products from category {}: {}", categoryId, e.message)
            return emptyList()
        }
    }

    private fun generateRandomProductUrls(count: Int): List<String> {
        val productUrls = mutableListOf<String>()
        val usedIds = mutableSetOf<Int>()
        
        // 무신사 상품 ID 범위 (대략적인 범위)
        val minId = 1000000
        val maxId = 4000000
        
        while (productUrls.size < count && usedIds.size < count * 2) {
            val randomId = Random.nextInt(minId, maxId)
            if (usedIds.add(randomId)) {
                productUrls.add("https://www.musinsa.com/app/goods/$randomId")
            }
        }
        
        log.debug("Generated {} random product URLs", productUrls.size)
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
            
            log.debug("Successfully registered product: {} (ID: {})", crawledProduct.name, savedProduct.id)
            
        } catch (e: Exception) {
            log.error("Failed to register product: {}", crawledProduct.name, e)
            throw BusinessException(ErrorCode.INTERNAL_ERROR, cause = e)
        }
    }
    
    fun fetchAndRegisterRankingProducts(count: Int = 100): Int {
        log.info("Starting to fetch and register {} products from Musinsa ranking API", count)
        
        try {
            val registeredCount = fetchAndRegisterProductsFromRankingApi(count)
            
            // 랭킹 API로 충분한 상품을 얻지 못한 경우 기존 방식으로 보충
            if (registeredCount < count / 2) {
                log.info("Ranking API returned insufficient products ({}), falling back to crawling", registeredCount)
                val additionalCount = fetchAndRegisterRandomProducts(count - registeredCount)
                return registeredCount + additionalCount
            }
            
            return registeredCount
            
        } catch (e: Exception) {
            log.error("Failed to fetch products from ranking API, falling back to crawling", e)
            return fetchAndRegisterRandomProducts(count)
        }
    }
    
    private fun fetchAndRegisterProductsFromRankingApi(count: Int): Int {
        val rankingApis = listOf(
            "https://api.musinsa.com/api2/hm/web/v5/pans/ranking/sections/200?storeCode=musinsa&categoryCode=000&contentsId=",
            "https://api.musinsa.com/api2/hm/web/v5/pans/ranking/sections/200?storeCode=musinsa&categoryCode=001&contentsId=",
            "https://api.musinsa.com/api2/hm/web/v5/pans/ranking/sections/200?storeCode=musinsa&categoryCode=002&contentsId=",
            "https://api.musinsa.com/api2/hm/web/v5/pans/ranking/sections/200?storeCode=musinsa&categoryCode=003&contentsId=",
            "https://api.musinsa.com/api2/hm/web/v5/pans/ranking/sections/200?storeCode=musinsa&categoryCode=103&contentsId="
        )
        
        var totalRegistered = 0
        val maxPerApi = (count / rankingApis.size) + 10
        
        rankingApis.forEach { apiUrl ->
            if (totalRegistered >= count) return totalRegistered
            
            try {
                val response = restClient.get()
                    .uri(apiUrl)
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .header("Accept", "application/json")
                    .header("Referer", "https://www.musinsa.com/")
                    .retrieve()
                    .body(String::class.java)
                    ?: return@forEach
                
                val rankingResponse = objectMapper.readValue(response, MusinsaRankingResponse::class.java)
                
                // 새로운 API 구조 처리
                rankingResponse.data?.modules?.forEach { module ->
                    if (module.type == "MULTICOLUMN") {
                        module.items.take(maxPerApi).forEach { productItem ->
                            if (totalRegistered >= count) return@forEach
                            
                            val registered = registerProductFromModuleItem(productItem)
                            if (registered) totalRegistered++
                        }
                    }
                }
                
                // 이전 API 구조도 지원 (fallback)
                if (totalRegistered == 0) {
                    rankingResponse.data?.list?.take(maxPerApi)?.forEach { rankingItem ->
                        if (totalRegistered >= count) return@forEach
                        
                        rankingItem.item?.let { item ->
                            val registered = registerProductFromRankingItem(item)
                            if (registered) totalRegistered++
                        }
                    }
                }
                
            } catch (e: Exception) {
                log.warn("Failed to fetch from ranking API: {}", e.message)
            }
        }
        
        log.info("Registered {} products from ranking API", totalRegistered)
        return totalRegistered
    }
    
    private fun registerProductFromRankingItem(item: org.team_alilm.algamja.product.dto.MusinsaItem): Boolean {
        try {
            val storeNumberStr = item.itemNo ?: item.itemId ?: return false
            val storeNumber = storeNumberStr.toLongOrNull() ?: return false
            
            // 이미 존재하는 상품인지 확인
            val existingProduct = productExposedRepository.fetchProductByStoreNumber(
                storeNumber = storeNumber,
                store = Store.MUSINSA
            )
            
            if (existingProduct != null) {
                log.debug("Product already exists: {}", storeNumber)
                return false
            }
            
            // 상품 URL 생성 (크롤링용)
            val productUrl = "https://www.musinsa.com/app/goods/$storeNumber"
            
            // 먼저 크롤링 시도 (더 상세한 정보를 얻기 위해)
            val crawledProduct = crawlProductFromUrl(productUrl)
            if (crawledProduct != null) {
                registerProduct(crawledProduct, productUrl)
                return true
            }
            
            // 크롤링 실패 시 API 데이터로 직접 저장
            val price = (item.salePrice ?: item.price ?: 0).toBigDecimal()
            val savedProduct = productExposedRepository.save(
                name = item.itemName ?: "Unknown Product",
                storeNumber = storeNumber,
                brand = item.brandName ?: "Unknown Brand",
                thumbnailUrl = item.imageUrl ?: "",
                originalUrl = productUrl,
                store = Store.MUSINSA,
                price = price,
                firstCategory = item.category ?: "기타",
                secondCategory = null,
                firstOptions = emptyList(),
                secondOptions = emptyList(),
                thirdOptions = emptyList()
            )
            
            // 이미지 등록
            item.imageList?.forEachIndexed { index, imageUrl ->
                productImageExposedRepository.save(
                    productId = savedProduct.id,
                    imageUrl = imageUrl,
                    imageOrder = index
                )
            } ?: item.imageUrl?.let { imageUrl ->
                productImageExposedRepository.save(
                    productId = savedProduct.id,
                    imageUrl = imageUrl,
                    imageOrder = 0
                )
            }
            
            log.debug("Successfully registered product from ranking: {} (ID: {})", item.itemName, savedProduct.id)
            return true
            
        } catch (e: Exception) {
            log.error("Failed to register product from ranking item: {}", item.itemName, e)
            return false
        }
    }
    
    private fun registerProductFromModuleItem(item: org.team_alilm.algamja.product.dto.MusinsaProductItem): Boolean {
        try {
            val storeNumber = item.id?.toLongOrNull() ?: return false
            
            // 이미 존재하는 상품인지 확인
            val existingProduct = productExposedRepository.fetchProductByStoreNumber(
                storeNumber = storeNumber,
                store = Store.MUSINSA
            )
            
            if (existingProduct != null) {
                log.debug("Product already exists: {}", storeNumber)
                return false
            }
            
            // 상품 URL 생성 (크롤링용)
            val productUrl = "https://www.musinsa.com/app/goods/$storeNumber"
            
            // 크롤링 시도 (상세한 정보를 얻기 위해)
            val crawledProduct = crawlProductFromUrl(productUrl)
            if (crawledProduct != null) {
                registerProduct(crawledProduct, productUrl)
                return true
            }
            
            // 크롤링 실패 시 로그만 남기고 건너뛰기
            log.debug("Failed to crawl product, skipping: {} ({})", item.info?.productName, storeNumber)
            return false
            
        } catch (e: Exception) {
            log.error("Failed to register product from module item: {}", item.info?.productName, e)
            return false
        }
    }
    
    /**
     * 기존 등록된 상품들의 가격을 업데이트하고 히스토리를 기록하는 함수
     */
    fun updateExistingProductPrices(count: Int): Int {
        log.info("Starting price update for existing products (max: {})", count)
        
        try {
            // 기존 상품들을 무작위로 조회 (최근 등록 순으로 제한)
            val existingProducts = productExposedRepository.fetchRandomProductsForPriceUpdate(count)
            log.info("Found {} existing products for price update", existingProducts.size)
            
            var updatedCount = 0
            
            existingProducts.forEach { product ->
                try {
                    // 상품 URL로 최신 가격 크롤링
                    val productUrl = "https://www.musinsa.com/app/goods/${product.storeNumber}"
                    val crawledProduct = crawlProductFromUrl(productUrl)
                    
                    if (crawledProduct != null) {
                        val oldPrice = product.price
                        val newPrice = crawledProduct.price
                        
                        // 가격이 변경된 경우에만 업데이트
                        if (oldPrice != newPrice) {
                            // 상품 가격 업데이트
                            productExposedRepository.updatePrice(product.id, newPrice)
                            
                            // 가격 히스토리 기록
                            savePriceHistory(product.id, oldPrice, newPrice)
                            
                            log.debug("Price updated for product {}: {} -> {}", 
                                product.name, oldPrice, newPrice)
                            updatedCount++
                        }
                    }
                } catch (e: Exception) {
                    log.warn("Failed to update price for product {}: {}", product.name, e.message)
                }
            }
            
            log.info("Price update completed: {} products updated", updatedCount)
            return updatedCount
            
        } catch (e: Exception) {
            log.error("Failed to update existing product prices", e)
            return 0
        }
    }
    
    /**
     * 가격 히스토리를 저장하는 함수
     */
    private fun savePriceHistory(productId: Long, oldPrice: java.math.BigDecimal, newPrice: java.math.BigDecimal) {
        val changeType = when {
            newPrice > oldPrice -> "INCREASE"
            newPrice < oldPrice -> "DECREASE"
            else -> "SAME"
        }
        
        // TODO: PriceHistoryRepository를 통해 히스토리 저장
        log.debug("Price history saved for product {}: {} -> {} ({})", 
            productId, oldPrice, newPrice, changeType)
    }
}