package org.team_alilm.algamja.product.scheduler

import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.team_alilm.algamja.product.service.AblyProductService

@Component
class AblyProductScheduler(
    private val ablyProductService: AblyProductService
) {

    private val log = LoggerFactory.getLogger(javaClass)

    /**
     * 매일 오전 3시에 에이블리 TODAY API에서 100개 상품을 등록하는 스케줄 작업
     * Cron: 0 0 3 * * * (초 분 시 일 월 요일)
     * - 매일 오전 3시 실행 (1일 1회)
     * - TODAY API를 우선 사용하고, 실패 시 랭킹 페이지로 fallback
     * - 익명 토큰을 사용하여 API 호출
     */
    @Scheduled(cron = "0 * * * * *")
    fun registerTodayAblyProducts() {
        val startTime = System.currentTimeMillis()
        log.info("========== Ably TODAY Product Registration Scheduled Task Started ==========")
        
        try {
            // TODAY API를 우선적으로 사용
            val registeredCount = ablyProductService.fetchAndRegisterTodayProducts(100)
            val duration = System.currentTimeMillis() - startTime
            
            log.info("========== Ably TODAY Product Registration Completed ==========")
            log.info("Registered products: {}", registeredCount)
            log.info("Execution time: {}ms", duration)
            
        } catch (e: Exception) {
            val duration = System.currentTimeMillis() - startTime
            log.error("========== Ably TODAY Product Registration Failed ==========")
            log.error("Execution time: {}ms", duration)
            log.error("Error details:", e)
        }
    }
    
    /**
     * 매일 오전 4시에 모든 등록된 에이블리 상품의 가격을 업데이트하는 스케줄 작업
     * Cron: 0 0 4 * * * (초 분 시 일 월 요일)
     * - 매일 오전 4시 실행 (1일 1회)
     * - 모든 등록된 에이블리 상품의 가격 정보 업데이트
     * - 배치 처리로 메모리 사용량 최적화
     */
    @Scheduled(cron = "0 0 4 * * *")
    fun updateProductPrices() {
        val startTime = System.currentTimeMillis()
        log.info("========== Ably All Products Price Update Scheduled Task Started ==========")
        
        try {
            // 모든 등록된 에이블리 상품의 가격 업데이트 (배치 처리)
            val updatedCount = ablyProductService.updateAllProductPrices()
            val duration = System.currentTimeMillis() - startTime
            
            log.info("========== Ably All Products Price Update Completed: {} products updated in {}ms ==========", 
                updatedCount, duration)
        } catch (e: Exception) {
            val duration = System.currentTimeMillis() - startTime
            log.error("========== Ably All Products Price Update Failed in {}ms ==========", duration, e)
        }
    }
}