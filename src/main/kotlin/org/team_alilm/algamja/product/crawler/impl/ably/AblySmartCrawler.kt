package org.team_alilm.algamja.product.crawler.impl.ably

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.team_alilm.algamja.common.exception.BusinessException
import org.team_alilm.algamja.common.exception.ErrorCode
import org.team_alilm.algamja.product.crawler.ProductCrawler
import org.team_alilm.algamja.product.crawler.dto.CrawledProduct

/**
 * 스마트 하이브리드 크롤러
 * 1. OkHttp with Brotli 지원 (AblyCrawlerEnhanced)
 * 2. 403/압축 이진 실패시 즉시 Selenium 폴백 (AblySeleniumEnhanced)
 * 
 * 주요 특징:
 * - Brotli 자동 디코딩
 * - 쿠키 재사용
 * - 403 WAF 대응
 * - 모바일 UA/Referer 설정
 * - 명시적 대기 및 스크롤
 */
@Component("ablySmartCrawler")
@Order(1) // 최우선 순위
class AblySmartCrawler : ProductCrawler {
    
    @Autowired(required = false)
    private lateinit var crawlerEnhanced: AblyCrawlerEnhanced
    
    @Autowired(required = false)
    private lateinit var seleniumEnhanced: AblySeleniumEnhanced
    
    private val log = LoggerFactory.getLogger(javaClass)
    
    override fun supports(url: String): Boolean {
        // Enhanced 크롤러들이 있으면 사용, 없으면 기본 크롤러 사용
        return if (::crawlerEnhanced.isInitialized) {
            crawlerEnhanced.supports(url)
        } else {
            url.contains("a-bly.com/goods/")
        }
    }
    
    override fun normalize(url: String): String {
        return if (::crawlerEnhanced.isInitialized) {
            crawlerEnhanced.normalize(url)
        } else {
            url.substringBefore("?")
        }
    }
    
    override fun fetch(url: String): CrawledProduct {
        val startTime = System.currentTimeMillis()
        
        log.info("🚀 Starting smart hybrid crawling for URL: {}", url)
        
        // 1차 시도: Enhanced HTTP with Brotli
        if (::crawlerEnhanced.isInitialized) {
            try {
                log.debug("Attempting enhanced HTTP crawling with Brotli support...")
                val result = crawlerEnhanced.fetch(url)
                
                val duration = System.currentTimeMillis() - startTime
                log.info("✅ HTTP crawling successful in {}ms", duration)
                return result
                
            } catch (e: Exception) {
                val errorMessage = e.message ?: "Unknown error"
                
                // 403, 압축 이진 데이터 의심, 빈 응답 등 감지
                val shouldFallback = when {
                    errorMessage.contains("403") -> {
                        log.warn("⚠️ 403 Forbidden detected, WAF blocking suspected")
                        true
                    }
                    errorMessage.contains("�") || errorMessage.contains("?�") -> {
                        log.warn("⚠️ Binary/corrupted response detected, possibly compressed")
                        true
                    }
                    e is BusinessException && e.errorCode == ErrorCode.CRAWLER_INVALID_RESPONSE -> {
                        log.warn("⚠️ Invalid response detected")
                        true
                    }
                    else -> {
                        log.warn("⚠️ HTTP crawling failed: {}", errorMessage)
                        true
                    }
                }
                
                if (shouldFallback) {
                    return attemptSeleniumFallback(url, startTime)
                }
                
                throw e
            }
        }
        
        // Enhanced 크롤러가 없으면 바로 Selenium 시도
        return attemptSeleniumFallback(url, startTime)
    }
    
    private fun attemptSeleniumFallback(url: String, startTime: Long): CrawledProduct {
        log.info("🔄 Falling back to enhanced Selenium crawling...")
        
        if (!::seleniumEnhanced.isInitialized) {
            log.error("❌ Selenium crawler not available")
            throw BusinessException(ErrorCode.CRAWLER_INVALID_RESPONSE)
        }
        
        try {
            val result = seleniumEnhanced.fetch(url)
            
            val duration = System.currentTimeMillis() - startTime
            log.info("✅ Selenium fallback successful in {}ms", duration)
            
            // 성공 통계 로깅 (선택사항)
            logCrawlingSuccess(url, duration, "selenium")
            
            return result
            
        } catch (e: Exception) {
            val duration = System.currentTimeMillis() - startTime
            log.error("❌ Both HTTP and Selenium failed after {}ms: {}", duration, e.message)
            
            // 실패 통계 로깅 (선택사항)
            logCrawlingFailure(url, duration, e.message ?: "Unknown error")
            
            if (e is BusinessException) {
                throw e
            } else {
                throw BusinessException(ErrorCode.CRAWLER_INVALID_RESPONSE)
            }
        }
    }
    
    private fun logCrawlingSuccess(url: String, duration: Long, method: String) {
        // 성공 메트릭 로깅 (Micrometer, CloudWatch 등과 연동 가능)
        log.debug("METRIC: crawling_success url={} method={} duration_ms={}", url, method, duration)
    }
    
    private fun logCrawlingFailure(url: String, duration: Long, error: String) {
        // 실패 메트릭 로깅
        log.debug("METRIC: crawling_failure url={} duration_ms={} error={}", url, duration, error)
    }
}