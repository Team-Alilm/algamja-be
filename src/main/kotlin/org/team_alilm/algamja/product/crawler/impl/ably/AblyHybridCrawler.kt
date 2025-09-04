package org.team_alilm.algamja.product.crawler.impl.ably

import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.team_alilm.algamja.common.exception.BusinessException
import org.team_alilm.algamja.common.exception.ErrorCode
import org.team_alilm.algamja.product.crawler.ProductCrawler
import org.team_alilm.algamja.product.crawler.dto.CrawledProduct

/**
 * Ably 하이브리드 크롤러
 * 1차: REST API 호출 (빠르고 효율적)
 * 2차: Selenium 백업 (API 실패 시 웹 페이지 크롤링)
 * 
 * EC2 환경에서 Cloudflare 우회와 안정성을 위해 설계됨
 */
@Component
@Order(1) // AblyCrawler보다 우선순위를 높게 설정
@ConditionalOnProperty(name = ["crawler.ably.fallback-to-selenium"], havingValue = "true", matchIfMissing = false)
class AblyHybridCrawler(
    private val ablyCrawler: AblyCrawler,
    private val ablySeleniumCrawler: AblySeleniumCrawler
) : ProductCrawler {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun supports(url: String): Boolean {
        // 두 크롤러 중 하나라도 지원하면 true
        return ablyCrawler.supports(url) || ablySeleniumCrawler.supports(url)
    }

    override fun normalize(url: String): String {
        // 기본적으로 API 크롤러의 정규화 사용
        return ablyCrawler.normalize(url)
    }

    override fun fetch(url: String): CrawledProduct {
        val startTime = System.currentTimeMillis()
        
        log.info("Starting hybrid crawling for URL: {}", url)
        
        // 1차 시도: REST API 크롤링
        try {
            log.debug("Attempting primary crawling with REST API...")
            val result = ablyCrawler.fetch(url)
            
            val duration = System.currentTimeMillis() - startTime
            log.info("✅ Primary API crawling successful for URL: {} in {}ms", url, duration)
            return result
            
        } catch (e: BusinessException) {
            log.warn("❌ Primary API crawling failed for URL: {}, error: {}", url, e.message)
            log.debug("API crawling exception details", e)
            
            // 특정 에러 코드에 대해서만 Selenium 백업 시도
            if (e.errorCode == ErrorCode.CRAWLER_INVALID_RESPONSE) {
                return attemptSeleniumFallback(url, startTime)
            } else {
                throw e
            }
            
        } catch (e: Exception) {
            log.warn("❌ Primary API crawling failed with unexpected error for URL: {}, error: {}", url, e.message)
            log.debug("Unexpected API crawling exception details", e)
            return attemptSeleniumFallback(url, startTime)
        }
    }

    private fun attemptSeleniumFallback(url: String, startTime: Long): CrawledProduct {
        log.info("🔄 Attempting fallback crawling with Selenium...")
        
        try {
            val result = ablySeleniumCrawler.fetch(url)
            
            val duration = System.currentTimeMillis() - startTime
            log.info("✅ Fallback Selenium crawling successful for URL: {} in {}ms", url, duration)
            return result
            
        } catch (e: Exception) {
            val duration = System.currentTimeMillis() - startTime
            log.error("❌ Both primary and fallback crawling failed for URL: {} after {}ms", url, duration, e)
            
            // 마지막으로 원본 에러를 그대로 던지거나 일반적인 크롤링 실패 에러를 던짐
            if (e is BusinessException) {
                throw e
            } else {
                throw BusinessException(ErrorCode.CRAWLER_INVALID_RESPONSE)
            }
        }
    }
}