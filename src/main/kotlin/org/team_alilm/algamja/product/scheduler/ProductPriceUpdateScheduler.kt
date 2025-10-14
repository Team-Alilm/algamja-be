package org.team_alilm.algamja.product.scheduler

import net.javacrumbs.shedlock.spring.annotation.SchedulerLock
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.team_alilm.algamja.product.service.ProductPriceUpdateService

@Component
class ProductPriceUpdateScheduler(
    private val productPriceUpdateService: ProductPriceUpdateService
) {

    private val log = LoggerFactory.getLogger(javaClass)

    /**
     * 매일 오전 7시에 모든 스토어의 등록된 상품 가격을 업데이트하는 통합 스케줄 작업
     * Cron: 0 0 7 * * * (초 분 시 일 월 요일)
     * - 매일 오전 7시 실행 (1일 1회)
     * - 모든 스토어(무신사, 지그재그, 29CM)의 상품을 통합적으로 처리
     * - 스토어별로 적절한 크롤러를 자동 선택하여 가격 정보 업데이트
     * - 가격 변경 시 히스토리 저장
     * - 배치 처리로 메모리 사용량 최적화
     */
    @Scheduled(cron = "0 0 7 * * *")
    @SchedulerLock(name = "productPriceUpdate", lockAtMostFor = "2h", lockAtLeastFor = "5m")
    fun updateAllProductPrices() {
        val startTime = System.currentTimeMillis()
        log.info("========== Unified Product Price Update Scheduled Task Started ==========")
        
        try {
            // 통합 가격 업데이트 서비스 호출
            val updatedCount = productPriceUpdateService.updateAllProductPrices()
            val duration = System.currentTimeMillis() - startTime
            
            log.info("========== Unified Product Price Update Completed ==========")
            log.info("Updated products: {}", updatedCount)
            log.info("Execution time: {}ms", duration)
            
        } catch (e: Exception) {
            val duration = System.currentTimeMillis() - startTime
            log.error("========== Unified Product Price Update Failed ==========")
            log.error("Execution time: {}ms", duration)
            log.error("Error details:", e)
        }
    }
    
}