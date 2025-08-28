package org.team_alilm.algamja.product.crawler

import org.team_alilm.algamja.product.crawler.dto.CrawledProduct

interface ProductCrawler {
    /** 이 URL을 내가 처리할 수 있나? (도메인/패턴 검사) */
    fun supports(url: String): Boolean

    /** 추출/정규화: 쿼리스트립, 리디렉션 제거 등 */
    fun normalize(url: String): String = url

    /** 실제 크롤링 */
    fun fetch(url: String): CrawledProduct
}