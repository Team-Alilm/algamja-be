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
     * 매일 오전 6시에 무신사 랭킹 API에서 100개 상품을 등록하는 스케줄 작업
     * Cron: 0 0 6 * * * (초 분 시 일 월 요일)
     * - 매일 오전 6시 실행 (1일 1회)
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
     * 하루 2회(오전 9시, 오후 6시) 기존 상품들의 가격을 업데이트하는 스케줄 작업
     * Cron: 0 0 9,18 * * * (초 분 시 일 월 요일)
     * - 오전 9시, 오후 6시 실행 (1일 2회)
     * - 기존 등록된 상품들의 가격 정보 업데이트 및 히스토리 저장
     */
    @Scheduled(cron = "0 0 9,18 * * *")
    fun updateProductPrices() {
        val startTime = System.currentTimeMillis()
        log.info("========== Musinsa Product Price Update Scheduled Task Started ==========")
        
        try {
            // 기존 등록된 상품들의 가격 업데이트 (최대 200개)
            val updatedCount = musinsaProductService.updateExistingProductPrices(200)
            val duration = System.currentTimeMillis() - startTime
            
            log.info("========== Musinsa Product Price Update Completed: $updatedCount products updated in ${duration}ms ==========")
        } catch (e: Exception) {
            val duration = System.currentTimeMillis() - startTime
            log.error("========== Musinsa Product Price Update Failed in ${duration}ms ==========", e)
        }
    }
}