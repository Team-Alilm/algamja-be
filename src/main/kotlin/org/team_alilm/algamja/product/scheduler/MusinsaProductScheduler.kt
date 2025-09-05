package org.team_alilm.algamja.product.scheduler

import net.javacrumbs.shedlock.spring.annotation.SchedulerLock
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.team_alilm.algamja.product.service.MusinsaProductService

@Component
class MusinsaProductScheduler(
    private val musinsaProductService: MusinsaProductService
) {

    private val log = LoggerFactory.getLogger(javaClass)

    /**
     * 매분 무신사 랭킹 API에서 100개 상품을 등록하는 스케줄 작업 (테스트용)
     * Cron: 0 * * * * * (초 분 시 일 월 요일)
     * - 매분 0초에 실행
     * - 랭킹 API를 우선 사용하고, 실패 시 크롤링으로 fallback
     */
    @Scheduled(cron = "0 0 6 * * *")
    @SchedulerLock(name = "musinsaProductRegistration", lockAtMostFor = "1h", lockAtLeastFor = "10m")
    fun registerRankingMusinsaProducts() {
        val startTime = System.currentTimeMillis()
        log.info("========== Musinsa Ranking Product Registration Scheduled Task Started ==========")
        
        try {
            // 랭킹 API를 우선적으로 사용
            val registeredCount = musinsaProductService.fetchAndRegisterRankingProducts(30)
            val duration = System.currentTimeMillis() - startTime
            
            log.info("========== Musinsa Ranking Product Registration Completed ==========")
            log.info("Registered products: {}", registeredCount)
            log.info("Execution time: {}ms", duration)
            
        } catch (e: Exception) {
            val duration = System.currentTimeMillis() - startTime
            log.error("========== Musinsa Ranking Product Registration Failed ==========")
            log.error("Execution time: {}ms", duration)
            log.error("Error details:", e)
        }
    }
    
    // 가격 업데이트 기능은 ProductPriceUpdateScheduler로 이관됨
    // 해당 스케줄러가 모든 스토어를 통합적으로 처리함
}