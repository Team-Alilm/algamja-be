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
    
    /**
     * 매일 오전 8시에 가격 업데이트 결과 요약 리포트 생성
     * Cron: 0 0 8 * * * (초 분 시 일 월 요일)  
     * - 전날 가격 업데이트 결과 분석
     * - 스토어별 업데이트 현황 정리
     * - 가격 변동이 큰 상품들 식별
     */
    @Scheduled(cron = "0 0 8 * * *")
    @SchedulerLock(name = "priceUpdateReport", lockAtMostFor = "30m", lockAtLeastFor = "2m")
    fun generatePriceUpdateReport() {
        val startTime = System.currentTimeMillis()
        log.info("========== Daily Price Update Report Generation Started ==========")
        
        try {
            // TODO: 가격 업데이트 리포트 생성 로직 구현
            // - 어제 업데이트된 상품 수
            // - 스토어별 통계
            // - 가격 변동률이 큰 상품들
            // - 업데이트 실패한 상품들
            
            val duration = System.currentTimeMillis() - startTime
            log.info("========== Daily Price Update Report Generated in {}ms ==========", duration)
            
        } catch (e: Exception) {
            val duration = System.currentTimeMillis() - startTime
            log.error("========== Daily Price Update Report Generation Failed in {}ms ==========", duration, e)
        }
    }
}