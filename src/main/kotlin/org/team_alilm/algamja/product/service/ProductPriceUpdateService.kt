package org.team_alilm.algamja.product.service

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
                
                // 배치 간 짧은 휴식 (서버 부하 방지)
                Thread.sleep(500)
            }
            
            log.info("All products price update completed: {} products updated", totalUpdatedCount)
            return totalUpdatedCount
            
        } catch (e: Exception) {
            log.error("Failed to update all product prices", e)
            return 0
        }
    }
    
    /**
     * 특정 스토어의 상품들 가격 업데이트
     */
    private fun updateProductsByStore(store: Store, products: List<ProductRow>): Int {
        var updatedCount = 0
        
        products.forEach { product ->
            try {
                val updated = updateSingleProductPrice(product)
                if (updated) updatedCount++
                
                // CPU 부하 방지를 위한 짧은 대기
                Thread.sleep(100)
                
            } catch (e: Exception) {
                log.warn("Failed to update price for product {} from {}: {}", 
                    product.name, store, e.message)
            }
        }
        
        log.debug("Updated {} out of {} products from store: {}", updatedCount, products.size, store)
        return updatedCount
    }
    
    /**
     * 단일 상품의 가격 업데이트
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

            // 가격이 실제로 변경되었는지 확인 (히스토리 기반 중복 방지 포함)
            if (shouldUpdatePrice(product.id, oldPrice, newPrice)) {
                // 상품 가격 업데이트
                productExposedRepository.updatePrice(product.id, newPrice)

                // 가격 히스토리 기록
                savePriceHistory(product.id, oldPrice, newPrice)

                log.debug("Price updated for product {} ({}): {} -> {}",
                    product.name, product.store, oldPrice, newPrice)
                return true
            }

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
     */
    private fun savePriceHistory(productId: Long, oldPrice: BigDecimal, newPrice: BigDecimal) {
        try {
            val changeType = when {
                newPrice > oldPrice -> "INCREASE"
                newPrice < oldPrice -> "DECREASE" 
                else -> "SAME"
            }
            
            // 가격 히스토리 저장
            val historyRow = productPriceHistoryRepository.recordPriceHistory(
                productId = productId,
                price = newPrice,
                recordedAt = System.currentTimeMillis()
            )
            
            log.debug("Price history saved for product {}: {} -> {} ({}) - History ID: {}", 
                productId, oldPrice, newPrice, changeType, historyRow.id)
                
        } catch (e: Exception) {
            log.error("Failed to save price history for product {}: {} -> {}", 
                productId, oldPrice, newPrice, e)
        }
    }
    
    /**
     * 가격 변경 여부를 확인하는 함수
     * 기존 히스토리와 비교하여 실제로 변경되었는지 확인
     */
    private fun shouldUpdatePrice(productId: Long, currentPrice: BigDecimal, newPrice: BigDecimal): Boolean {
        // 현재 상품의 가격과 새로운 가격이 다른 경우에만 업데이트
        if (currentPrice.compareTo(newPrice) == 0) {
            return false
        }
        
        // 히스토리에서 중복 기록 방지 - 최신 기록과 동일한 가격인지 확인
        return try {
            productPriceHistoryRepository.hasPriceChanged(productId, newPrice)
        } catch (e: Exception) {
            log.warn("Failed to check price history for product {}, proceeding with update", productId, e)
            true // 히스토리 확인 실패 시에는 업데이트 진행
        }
    }
    
    /**
     * 특정 상품의 가격 히스토리 조회 (디버깅/모니터링용)
     */
    fun getPriceHistory(productId: Long, limit: Int = 10): List<String> {
        return try {
            productPriceHistoryRepository.fetchPriceHistoryByProductId(productId, limit)
                .map { history ->
                    "ID: ${history.id}, Price: ${history.price}, Recorded: ${
                        java.time.Instant.ofEpochMilli(history.recordedAt)
                    }"
                }
        } catch (e: Exception) {
            log.error("Failed to fetch price history for product {}", productId, e)
            emptyList()
        }
    }
}