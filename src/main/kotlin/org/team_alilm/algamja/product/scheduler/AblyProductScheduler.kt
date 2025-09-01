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
     * 매 정각 + 10분에 에이블리에서 무작위로 50개 상품을 등록하는 스케줄 작업
     * Cron: 0 10 * * * * (초 분 시 일 월 요일)
     * - 0초 10분 매시간 실행
     */
    @Scheduled(cron = "0 10 * * * *")
    fun registerRandomAblyProducts() {
        val startTime = System.currentTimeMillis()
        log.info("========== Ably Product Registration Scheduled Task Started ==========")
        
        try {
            val registeredCount = ablyProductService.fetchAndRegisterRandomProducts(50)
            val duration = System.currentTimeMillis() - startTime
            
            log.info("========== Ably Product Registration Completed ==========")
            log.info("Registered products: {}", registeredCount)
            log.info("Execution time: {}ms", duration)
            
        } catch (e: Exception) {
            val duration = System.currentTimeMillis() - startTime
            log.error("========== Ably Product Registration Failed ==========")
            log.error("Execution time: {}ms", duration)
            log.error("Error details:", e)
        }
    }
}