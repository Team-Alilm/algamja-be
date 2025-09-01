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
class MusinsaProductService(
    private val restClient: RestClient,
    private val crawlerRegistry: CrawlerRegistry,
    private val productExposedRepository: ProductExposedRepository,
    private val productImageExposedRepository: ProductImageExposedRepository
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
        
        try {
            categories.forEach { categoryId ->
                val urls = fetchProductUrlsFromCategory(categoryId, count / categories.size + 5)
                productUrls.addAll(urls)
            }
            
            // 랜덤하게 섞고 요청된 개수만큼 선택
            return productUrls.shuffled().take(count)
            
        } catch (e: Exception) {
            log.error("Failed to fetch product URLs from categories", e)
            
            // 대안: 랜덤 상품 ID 생성으로 URL 만들기
            log.info("Falling back to random product ID generation")
            return generateRandomProductUrls(count)
        }
    }

    private fun fetchProductUrlsFromCategory(categoryId: String, limit: Int): List<String> {
        val productUrls = mutableListOf<String>()
        
        try {
            // 무신사 카테고리 페이지에서 상품 URL들을 추출
            val page = Random.nextInt(1, 6) // 1-5 페이지 중 랜덤 선택
            val categoryUrl = "https://www.musinsa.com/categories/item/$categoryId?d_cat_cd=$categoryId&brand=&list_kind=small&sort=sale&sub_sort=&page=$page&display_cnt=90"
            
            log.debug("Fetching products from category URL: {}", categoryUrl)
            
            val response = restClient.get()
                .uri(categoryUrl)
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
            
            log.debug("Extracted {} product URLs from category {}", productUrls.size, categoryId)
            
        } catch (e: Exception) {
            log.warn("Failed to fetch products from category {}: {}", categoryId, e.message)
        }
        
        return productUrls
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
}