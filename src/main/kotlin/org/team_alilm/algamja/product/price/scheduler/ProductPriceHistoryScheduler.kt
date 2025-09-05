package org.team_alilm.algamja.product.price.scheduler

import net.javacrumbs.shedlock.spring.annotation.SchedulerLock
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import org.team_alilm.algamja.product.price.repository.ProductPriceHistoryRepository
import org.team_alilm.algamja.product.repository.ProductExposedRepository
import java.time.LocalDateTime

@Component
class ProductPriceHistoryScheduler(
    private val productExposedRepository: ProductExposedRepository,
    private val productPriceHistoryRepository: ProductPriceHistoryRepository
) {
    
    private val log = LoggerFactory.getLogger(javaClass)
    
    @Scheduled(cron = "0 0 2 * * *") // 매일 새벽 2시에 실행
    @SchedulerLock(name = "priceHistoryCollection", lockAtMostFor = "1h", lockAtLeastFor = "10m")
    @Transactional
    fun collectDailyPriceHistory() {
        val startTime = System.currentTimeMillis()
        log.info("Starting daily price history collection at {}", LocalDateTime.now())
        
        try {
            val products = productExposedRepository.fetchAllActiveProducts()
            var successCount = 0
            var errorCount = 0
            
            products.forEach { product ->
                try {
                    val currentPrice = product.price
                    
                    productPriceHistoryRepository.recordPriceHistory(
                        productId = product.id,
                        price = currentPrice
                    )
                    
                    successCount++
                    
                    if (successCount % 100 == 0) {
                        log.info("Processed {} products for price history", successCount)
                    }
                } catch (e: Exception) {
                    errorCount++
                    log.error("Failed to record price history for product {}: {}", 
                        product.id, e.message)
                }
            }
            
            val elapsedTime = System.currentTimeMillis() - startTime
            log.info("Daily price history collection completed. Success: {}, Errors: {}, Time: {}ms", 
                successCount, errorCount, elapsedTime)
            
        } catch (e: Exception) {
            log.error("Failed to complete daily price history collection", e)
        }
    }
    
    @Scheduled(cron = "0 0 3 1 * *") // 매월 1일 새벽 3시에 오래된 데이터 정리
    @SchedulerLock(name = "priceHistoryCleanup", lockAtMostFor = "30m", lockAtLeastFor = "5m")
    @Transactional
    fun cleanupOldPriceHistory() {
        log.info("Starting old price history cleanup at {}", LocalDateTime.now())
        
        try {
            // 6개월 이상 된 데이터 삭제 (필요에 따라 조정)
            val sixMonthsAgo = System.currentTimeMillis() - (180L * 24 * 60 * 60 * 1000)
            val deletedCount = productPriceHistoryRepository.deleteOldPriceHistory(sixMonthsAgo)
            
            log.info("Deleted {} old price history records", deletedCount)
        } catch (e: Exception) {
            log.error("Failed to cleanup old price history", e)
        }
    }
}