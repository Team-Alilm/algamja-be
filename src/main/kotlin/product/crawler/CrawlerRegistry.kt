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

    /**
     * Selects the first registered ProductCrawler that supports the given product URL.
     *
     * Iterates the injected crawlers and returns the first whose `supports(url)` is true.
     *
     * @param url The product page URL to find a matching crawler for.
     * @return The first ProductCrawler that supports the provided URL.
     * @throws BusinessException with ErrorCode.CRAWLER_NOT_FOUND if no crawler supports the URL.
     */
    fun resolve(url: String): ProductCrawler {
        return crawlers.firstOrNull { it.supports(url) }
            ?: run {
                log.warn("지원하는 크롤러가 없습니다. URL: $url")
                throw BusinessException(ErrorCode.CRAWLER_NOT_FOUND)
            }
    }
}