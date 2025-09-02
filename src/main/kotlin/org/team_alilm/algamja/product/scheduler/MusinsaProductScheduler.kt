package org.team_alilm.algamja.product.scheduler

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
    fun registerRankingMusinsaProducts() {
        val startTime = System.currentTimeMillis()
        log.info("========== Musinsa Ranking Product Registration Scheduled Task Started ==========")
        
        try {
            // 랭킹 API를 우선적으로 사용
            val registeredCount = musinsaProductService.fetchAndRegisterRankingProducts(100)
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
    
    /**
     * 매일 오전 7시에 모든 등록된 상품의 가격을 업데이트하는 스케줄 작업
     * Cron: 0 0 7 * * * (초 분 시 일 월 요일)
     * - 매일 오전 7시 실행 (1일 1회)
     * - 모든 등록된 상품의 가격 정보 업데이트 및 히스토리 저장
     * - 배치 처리로 메모리 사용량 최적화
     */
    @Scheduled(cron = "0 0 7 * * *")
    fun updateProductPrices() {
        val startTime = System.currentTimeMillis()
        log.info("========== Musinsa All Products Price Update Scheduled Task Started ==========")
        
        try {
            // 모든 등록된 상품의 가격 업데이트 (배치 처리)
            val updatedCount = musinsaProductService.updateAllProductPrices()
            val duration = System.currentTimeMillis() - startTime
            
            log.info("========== Musinsa All Products Price Update Completed: $updatedCount products updated in ${duration}ms ==========")
        } catch (e: Exception) {
            val duration = System.currentTimeMillis() - startTime
            log.error("========== Musinsa All Products Price Update Failed in ${duration}ms ==========", e)
        }
    }
}