package org.team_alilm.product.crawler

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.team_alilm.common.exception.BusinessException
import org.team_alilm.common.exception.ErrorCode

@Component
class CrawlerRegistry(
    private val crawlers: List<ProductCrawler>
) {

    private val log = LoggerFactory.getLogger(javaClass)

    fun resolve(url: String): ProductCrawler {
        log.debug("Resolving crawler for URL: {}", url)
        log.trace("Available crawlers: {}", crawlers.map { it::class.simpleName })
        
        val supportedCrawlers = crawlers.filter { crawler ->
            val supports = crawler.supports(url)
            log.trace("Crawler {} supports URL {}: {}", crawler::class.simpleName, url, supports)
            supports
        }
        
        return when (supportedCrawlers.size) {
            0 -> {
                log.warn("No crawler found for URL: {} (tried {} crawlers)", url, crawlers.size)
                throw BusinessException(ErrorCode.CRAWLER_NOT_FOUND)
            }
            1 -> {
                val selectedCrawler = supportedCrawlers.first()
                log.info("Selected crawler: {} for URL: {}", selectedCrawler::class.simpleName, url)
                selectedCrawler
            }
            else -> {
                val selectedCrawler = supportedCrawlers.first()
                log.warn("Multiple crawlers support URL: {} - using first one: {} (alternatives: {})", 
                        url, selectedCrawler::class.simpleName, 
                        supportedCrawlers.drop(1).map { it::class.simpleName })
                selectedCrawler
            }
        }
    }
}