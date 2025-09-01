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
     * 매 정각 + 10분에 무신사에서 무작위로 100개 상품을 등록하는 스케줄 작업
     * Cron: 0 10 * * * * (초 분 시 일 월 요일)
     * - 0초 10분 매시간 실행
     */
    @Scheduled(cron = "0 10 * * * *")
    fun registerRandomMusinsaProducts() {
        val startTime = System.currentTimeMillis()
        log.info("========== Musinsa Product Registration Scheduled Task Started ==========")
        
        try {
            val registeredCount = musinsaProductService.fetchAndRegisterRandomProducts(100)
            val duration = System.currentTimeMillis() - startTime
            
            log.info("========== Musinsa Product Registration Completed ==========")
            log.info("Registered products: {}", registeredCount)
            log.info("Execution time: {}ms", duration)
            
        } catch (e: Exception) {
            val duration = System.currentTimeMillis() - startTime
            log.error("========== Musinsa Product Registration Failed ==========")
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