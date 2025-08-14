package org.team_alilm.common.config

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
            .defaultHeader(HttpHeaders.USER_AGENT, "Mozilla/5.0")
            .requestFactory(simpleClientHttpRequestFactory())
            .requestInterceptor { req, body, exec ->
                if (log.isDebugEnabled) {
                    log.debug("[REST-REQUEST] {} {}", req.method, req.uri)
                }
                exec.execute(req, body)
            }
            .build()

    private fun simpleClientHttpRequestFactory(): ClientHttpRequestFactory =
        SimpleClientHttpRequestFactory().apply {
            setConnectTimeout(5000)
            setReadTimeout(10000)
        }
}