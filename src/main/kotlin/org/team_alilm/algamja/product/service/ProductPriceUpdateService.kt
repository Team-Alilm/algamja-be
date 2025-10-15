package org.team_alilm.algamja.product.service

import kotlinx.coroutines.*
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.team_alilm.algamja.common.enums.Store
import org.team_alilm.algamja.product.crawler.CrawlerRegistry
import org.team_alilm.algamja.product.entity.ProductRow
import org.team_alilm.algamja.product.repository.ProductExposedRepository
import org.team_alilm.algamja.product.price.repository.ProductPriceHistoryRepository
import java.math.BigDecimal

@Service
@Transactional
class ProductPriceUpdateService(
    private val productExposedRepository: ProductExposedRepository,
    private val crawlerRegistry: CrawlerRegistry,
    private val productPriceHistoryRepository: ProductPriceHistoryRepository
) {

    private val log = LoggerFactory.getLogger(javaClass)

    /**
     * 모든 등록된 상품의 가격을 배치로 업데이트하고 히스토리를 기록하는 함수
     * 스토어에 관계없이 통합적으로 처리하며, 메모리 사용량을 줄이기 위해 배치 단위로 처리
     */
    fun updateAllProductPrices(): Int {
        log.info("Starting unified price update for all existing products")
        
        try {
            val batchSize = 50 // 배치 크기 (저사양 서버 고려)
            var offset = 0
            var totalUpdatedCount = 0
            
            while (true) {
                // 배치 단위로 상품 조회 (모든 스토어)
                val productBatch = productExposedRepository.fetchProductsForPriceUpdateBatch(batchSize, offset)
                
                if (productBatch.isEmpty()) {
                    break // 더 이상 처리할 상품이 없음
                }
                
                log.info("Processing batch: {} products (offset: {})", productBatch.size, offset)
                
                // 스토어별로 그룹핑하여 효율적으로 처리
                val productsByStore = productBatch.groupBy { it.store }
                var batchUpdatedCount = 0
                
                productsByStore.forEach { (store, storeProducts) ->
                    log.debug("Processing {} products from store: {}", storeProducts.size, store)
                    batchUpdatedCount += updateProductsByStore(store, storeProducts)
                }
                
                totalUpdatedCount += batchUpdatedCount
                offset += batchSize
                
                log.info("Batch completed: {} products updated in this batch", batchUpdatedCount)

                // 배치 간 휴식 (서버 부하 및 rate limiting 방지 - 무신사는 더 긴 지연)
                val sleepTime = if (productsByStore.containsKey(Store.MUSINSA)) 2000L else 500L
                Thread.sleep(sleepTime)
            }
            
            log.info("All products price update completed: {} products updated", totalUpdatedCount)
            return totalUpdatedCount
            
        } catch (e: Exception) {
            log.error("Failed to update all product prices", e)
            return 0
        }
    }
    
    /**
     * 특정 스토어의 상품들 가격 업데이트 (병렬 처리)
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    private fun updateProductsByStore(store: Store, products: List<ProductRow>): Int = runBlocking {
        // 무신사 rate limiting 고려하여 병렬 처리 수 제한 (최대 2개)
        val parallelism = if (store == Store.MUSINSA) 2 else Runtime.getRuntime().availableProcessors().coerceAtMost(6)
        val dispatcher = Dispatchers.IO.limitedParallelism(parallelism)

        val results = products.chunked(10).flatMap { productChunk ->
            productChunk.map { product ->
                async(dispatcher) {
                    try {
                        updateSingleProductPrice(product)
                    } catch (e: Exception) {
                        log.warn("Failed to update price for product {} from {}: {}",
                            product.name, store, e.message)
                        false
                    }
                }
            }.awaitAll()
        }

        val updatedCount = results.count { it }
        log.debug("Updated {} out of {} products from store: {}", updatedCount, products.size, store)
        return@runBlocking updatedCount
    }
    
    /**
     * 단일 상품의 가격 업데이트
     * 가격 변경 여부와 관계없이 매일 히스토리를 저장하여 연속적인 가격 데이터 확보
     */
    private fun updateSingleProductPrice(product: ProductRow): Boolean {
        try {
            // 상품 URL 생성
            val productUrl = buildProductUrl(product)

            // 크롤러 레지스트리를 통해 적절한 크롤러 선택
            val crawler = crawlerRegistry.resolve(productUrl)
            val normalizedUrl = crawler.normalize(productUrl)

            // 최신 가격 크롤링
            val crawledProduct = crawler.fetch(normalizedUrl)

            val oldPrice = product.price
            val newPrice = crawledProduct.price
            val priceChanged = oldPrice.compareTo(newPrice) != 0

            // 코루틴 내에서 실행되므로 명시적으로 트랜잭션 생성
            org.jetbrains.exposed.sql.transactions.transaction {
                // 가격이 변경된 경우 상품 테이블 업데이트
                if (priceChanged) {
                    productExposedRepository.updatePrice(product.id, newPrice)
                    log.debug("Price updated for product {} ({}): {} -> {}",
                        product.name, product.store, oldPrice, newPrice)
                }

                // 가격 변경 여부와 관계없이 매일 히스토리 저장 (그래프 연속성 확보)
                savePriceHistory(product.id, newPrice)
            }

            return true

        } catch (e: Exception) {
            log.warn("Failed to update price for product {}: {}", product.name, e.message)
        }

        return false
    }
    
    /**
     * 상품의 스토어에 따라 올바른 URL 생성
     */
    private fun buildProductUrl(product: ProductRow): String {
        return when (product.store) {
            Store.MUSINSA -> "https://www.musinsa.com/app/goods/${product.storeNumber}"
            Store.ZIGZAG -> "https://zigzag.kr/catalog/products/${product.storeNumber}"
            Store.CM29 -> "https://shop.29cm.co.kr/product/${product.storeNumber}"
        }
    }
    
    /**
     * 가격 히스토리를 저장하는 함수
     * 매일 스냅샷을 저장하여 그래프용 연속 데이터 확보
     */
    private fun savePriceHistory(productId: Long, price: BigDecimal) {
        try {
            // 가격 히스토리 저장 (매일 실행되므로 recordedAt은 현재 시간)
            val historyRow = productPriceHistoryRepository.recordPriceHistory(
                productId = productId,
                price = price,
                recordedAt = System.currentTimeMillis()
            )

            log.debug("Daily price snapshot saved for product {}: {} - History ID: {}",
                productId, price, historyRow.id)

        } catch (e: Exception) {
            log.error("Failed to save price history for product {}: {}",
                productId, price, e)
        }
    }
}