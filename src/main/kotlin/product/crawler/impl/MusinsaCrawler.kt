package org.team_alilm.product.crawler.impl

import org.team_alilm.product.crawler.ProductCrawler
import org.team_alilm.product.crawler.dto.CrawledProduct
import java.net.URI

class MusinsaCrawler : ProductCrawler {

    override fun supports(url: String): Boolean {
        val host = runCatching { URI(url).host.orEmpty().lowercase() }.getOrDefault("")
        // 서브도메인 포함 매칭
        return host == "musinsa.com" || host.endsWith(".musinsa.com")
    }

    /** 쿼리스트링/프래그먼트 제거 + 스킴 보정 */
    override fun normalize(url: String): String {
        val u = URI(url)
        val scheme = u.scheme ?: "https"
        return URI(
            scheme,
            u.userInfo,
            u.host,
            u.port,
            u.path,
            /* query */ null,
            /* fragment */ null
        ).toString()
    }

    override fun fetch(url: String): CrawledProduct {
        TODO("Not yet implemented")
    }
}