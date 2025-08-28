package org.team_alilm.product.crawler

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.team_alilm.common.exception.BusinessException
import common.exception.ErrorCode

@Component
class CrawlerRegistry(
    private val crawlers: List<ProductCrawler>
) {

    private val log = LoggerFactory.getLogger(javaClass)

    fun resolve(url: String): ProductCrawler {
        return crawlers.firstOrNull { it.supports(url) }
            ?: run {
                log.warn("지원하는 크롤러가 없습니다. URL: $url")
                throw BusinessException(ErrorCode.CRAWLER_NOT_FOUND)
            }
    }
}