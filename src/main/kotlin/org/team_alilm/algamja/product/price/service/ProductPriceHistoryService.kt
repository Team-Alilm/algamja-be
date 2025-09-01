package org.team_alilm.algamja.product.price.service

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.team_alilm.algamja.common.exception.BusinessException
import org.team_alilm.algamja.common.exception.ErrorCode
import org.team_alilm.algamja.product.price.repository.ProductPriceHistoryRepository
import org.team_alilm.algamja.product.repository.ProductExposedRepository
import java.math.BigDecimal

@Service
@Transactional(readOnly = true)
class ProductPriceHistoryService(
    private val productPriceHistoryRepository: ProductPriceHistoryRepository,
    private val productExposedRepository: ProductExposedRepository
) {

    private val log = LoggerFactory.getLogger(javaClass)

    /**
     * 상품의 가격 변동을 기록합니다.
     * 이전 가격과 다른 경우에만 기록합니다.
     */
    @Transactional
    fun recordPriceIfChanged(productId: Long, newPrice: BigDecimal): Boolean {
        try {
            // 상품 존재 여부 확인
            val product = productExposedRepository.fetchProductById(productId)
                ?: throw BusinessException(ErrorCode.PRODUCT_NOT_FOUND)

            // 가격 변동이 있는지 확인
            val hasChanged = productPriceHistoryRepository.hasPriceChanged(productId, newPrice)
            
            if (hasChanged) {
                // 가격 히스토리 기록
                val priceHistory = productPriceHistoryRepository.recordPriceHistory(productId, newPrice)
                
                log.info("Price history recorded for product {}: {} -> {}", 
                    productId, product.price, newPrice)
                
                // 상품의 현재 가격도 업데이트 (필요한 경우)
                // productExposedRepository.updatePrice(productId, newPrice)
                
                return true
            }
            
            log.debug("No price change for product {}: {}", productId, newPrice)
            return false
            
        } catch (e: Exception) {
            log.error("Failed to record price history for product {}", productId, e)
            throw BusinessException(ErrorCode.INTERNAL_ERROR, cause = e)
        }
    }

    /**
     * 상품의 가격 히스토리를 조회합니다.
     */
    fun getPriceHistory(productId: Long, limit: Int = 30): List<PriceHistoryDto> {
        // 상품 존재 여부 확인
        productExposedRepository.fetchProductById(productId)
            ?: throw BusinessException(ErrorCode.PRODUCT_NOT_FOUND)

        val histories = productPriceHistoryRepository.fetchPriceHistoryByProductId(productId, limit)
        
        return histories.map { history ->
            PriceHistoryDto(
                id = history.id,
                productId = history.productId,
                price = history.price.toLong(),
                recordedAt = history.recordedAt,
                recordedDate = java.time.Instant.ofEpochMilli(history.recordedAt).toString()
            )
        }
    }

    /**
     * 특정 기간의 가격 히스토리를 조회합니다.
     */
    fun getPriceHistoryByPeriod(
        productId: Long,
        startTime: Long,
        endTime: Long
    ): List<PriceHistoryDto> {
        // 상품 존재 여부 확인
        productExposedRepository.fetchProductById(productId)
            ?: throw BusinessException(ErrorCode.PRODUCT_NOT_FOUND)

        val histories = productPriceHistoryRepository.fetchPriceHistoryByProductIdAndPeriod(
            productId, startTime, endTime
        )
        
        return histories.map { history ->
            PriceHistoryDto(
                id = history.id,
                productId = history.productId,
                price = history.price.toLong(),
                recordedAt = history.recordedAt,
                recordedDate = java.time.Instant.ofEpochMilli(history.recordedAt).toString()
            )
        }
    }

    /**
     * 가격 변동 통계를 조회합니다.
     */
    fun getPriceStats(productId: Long): PriceStatsDto {
        // 상품 존재 여부 확인
        val product = productExposedRepository.fetchProductById(productId)
            ?: throw BusinessException(ErrorCode.PRODUCT_NOT_FOUND)

        val histories = productPriceHistoryRepository.fetchPriceHistoryByProductId(productId)
        
        if (histories.isEmpty()) {
            return PriceStatsDto(
                productId = productId,
                currentPrice = product.price.toLong(),
                minPrice = product.price.toLong(),
                maxPrice = product.price.toLong(),
                avgPrice = product.price.toLong(),
                priceChangeCount = 0,
                firstRecordedAt = null,
                lastRecordedAt = null
            )
        }
        
        val prices = histories.map { it.price.toLong() }
        val minPrice = prices.minOrNull() ?: product.price.toLong()
        val maxPrice = prices.maxOrNull() ?: product.price.toLong()
        val avgPrice = prices.average().toLong()
        
        return PriceStatsDto(
            productId = productId,
            currentPrice = product.price.toLong(),
            minPrice = minPrice,
            maxPrice = maxPrice,
            avgPrice = avgPrice,
            priceChangeCount = histories.size,
            firstRecordedAt = histories.lastOrNull()?.recordedAt,
            lastRecordedAt = histories.firstOrNull()?.recordedAt
        )
    }
}

data class PriceHistoryDto(
    val id: Long,
    val productId: Long,
    val price: Long,
    val recordedAt: Long,
    val recordedDate: String
)

data class PriceStatsDto(
    val productId: Long,
    val currentPrice: Long,
    val minPrice: Long,
    val maxPrice: Long,
    val avgPrice: Long,
    val priceChangeCount: Int,
    val firstRecordedAt: Long?,
    val lastRecordedAt: Long?
)