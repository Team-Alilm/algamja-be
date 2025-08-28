package org.team_alilm.product.crawler

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient

@Component
class CrawlerHttp(
    private val restClient: RestClient
) {

    fun getDocument(url: String): Document {
        val html = restClient.get()
            .uri(url)
            .retrieve()
            .body(String::class.java)
            ?: error("Empty body for $url")
        return Jsoup.parse(html, url)
    }
}