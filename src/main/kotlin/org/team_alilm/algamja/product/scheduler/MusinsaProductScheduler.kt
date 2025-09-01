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
     * 매 정각 + 0분에 무신사 랭킹 API에서 100개 상품을 등록하는 스케줄 작업
     * Cron: 0 0 * * * * (초 분 시 일 월 요일)
     * - 0초 0분 매시간 실행
     * - 랭킹 API를 우선 사용하고, 실패 시 크롤링으로 fallback
     */
    @Scheduled(cron = "0 * * * * *")
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
     * 테스트용 스케줄러 - 매 5분마다 10개 상품 등록
     * 개발/테스트 환경에서만 사용 (프로덕션에서는 주석 처리 또는 profile로 분리)
     */
//    @Scheduled(fixedRate = 300000) // 5분 = 300,000ms
//    fun registerTestMusinsaProducts() {
//        val startTime = System.currentTimeMillis()
//        log.info("========== Test Musinsa Product Registration Started ==========")
//        
//        try {
//            val registeredCount = musinsaProductService.fetchAndRegisterRandomProducts(10)
//            val duration = System.currentTimeMillis() - startTime
//            
//            log.info("========== Test Musinsa Product Registration Completed ==========")
//            log.info("Registered products: {}", registeredCount)
//            log.info("Execution time: {}ms", duration)
//            
//        } catch (e: Exception) {
//            val duration = System.currentTimeMillis() - startTime
//            log.error("========== Test Musinsa Product Registration Failed ==========")
//            log.error("Execution time: {}ms", duration)
//            log.error("Error details:", e)
//        }
//    }
}