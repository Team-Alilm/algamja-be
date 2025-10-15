package org.team_alilm.algamja.common.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.http.client.ClientHttpRequestFactory
import org.springframework.http.client.SimpleClientHttpRequestFactory
import org.springframework.web.client.RestClient
import org.slf4j.LoggerFactory

@Configuration
class RestClientConfig {
    private val log = LoggerFactory.getLogger(RestClientConfig::class.java)

    @Bean
    fun restClient(): RestClient =
        RestClient.builder()
            .defaultHeader(HttpHeaders.USER_AGENT, "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
            .defaultHeader(HttpHeaders.ACCEPT, "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8")
            .defaultHeader(HttpHeaders.ACCEPT_LANGUAGE, "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7")
            .defaultHeader(HttpHeaders.ACCEPT_ENCODING, "gzip, deflate, br")
            .defaultHeader("Sec-Ch-Ua", "\"Not_A Brand\";v=\"8\", \"Chromium\";v=\"120\", \"Google Chrome\";v=\"120\"")
            .defaultHeader("Sec-Ch-Ua-Mobile", "?0")
            .defaultHeader("Sec-Ch-Ua-Platform", "\"Windows\"")
            .defaultHeader("Sec-Fetch-Dest", "document")
            .defaultHeader("Sec-Fetch-Mode", "navigate")
            .defaultHeader("Sec-Fetch-Site", "none")
            .defaultHeader("Sec-Fetch-User", "?1")
            .defaultHeader("Upgrade-Insecure-Requests", "1")
            .requestFactory(simpleClientHttpRequestFactory())
            .requestInterceptor { req, body, exec ->
                if (log.isDebugEnabled) {
                    log.debug("[REST-REQUEST] {} {}", req.method, req.uri)
                }
                exec.execute(req, body)
            }
            .build()

    @Bean
    fun simpleClientHttpRequestFactory(): ClientHttpRequestFactory =
        SimpleClientHttpRequestFactory().apply {
            setConnectTimeout(5000)
            setReadTimeout(10000)
        }
}