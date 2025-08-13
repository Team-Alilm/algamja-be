package org.team_alilm.product.crawler

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import java.time.Duration

@Component
class CrawlerHttp(
    restClientBuilder: RestClient.Builder
) {

    private val client: RestClient = restClientBuilder
        .baseUrl("") // baseUrl 필요 없으면 비워둠
        .requestFactory { factory ->
            factory.setConnectTimeout(Duration.ofSeconds(5))
            factory.setReadTimeout(Duration.ofSeconds(10))
        }
        .defaultHeader("User-Agent", "Mozilla/5.0 (compatible; AlilmBot/1.0)")
        .build()

    fun getDocument(url: String): Document {
        val html = client.get()
            .uri(url)
            .retrieve()
            .body(String::class.java)
            ?: throw IllegalStateException("Empty body for $url")

        return Jsoup.parse(html, url) // baseUri 설정
    }
}