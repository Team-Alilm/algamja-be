package org.team_alilm.algamja.product.crawler.impl.ably

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient

@Component
class AblyTokenManager(
    private val restClient: RestClient
) {
    
    private val log = LoggerFactory.getLogger(javaClass)
    
    // Rate Limit을 고려한 캐싱 (10분) - 토큰 API의 429 에러 방지
    private var cachedToken: String? = null
    private var tokenTime: Long = 0
    private val cacheTimeMs = 600_000 // 10분 (너무 짧으면 429 에러)
    
    fun getToken(): String {
        val now = System.currentTimeMillis()
        
        if (cachedToken != null && (now - tokenTime) < cacheTimeMs) {
            log.debug("Using recently cached Ably token ({}ms ago)", now - tokenTime)
            return cachedToken!!
        }
        
        log.debug("Fetching fresh Ably token (cache expired or not found)")
        val newToken = refreshToken()
        
        cachedToken = newToken
        tokenTime = now
        
        return newToken
    }
    
    /**
     * 403 에러 등으로 토큰이 유효하지 않을 때 강제 갱신
     */
    fun forceRefreshToken(): String {
        log.info("Force refreshing Ably token due to authentication error")
        
        // 캐시 무효화
        cachedToken = null
        tokenTime = 0
        
        val newToken = refreshToken()
        
        cachedToken = newToken
        tokenTime = System.currentTimeMillis()
        
        return newToken
    }
    
    private fun refreshToken(): String {
        val startTime = System.currentTimeMillis()
        
        return try {
            log.debug("Requesting new Ably anonymous token from API...")
            
            val response = restClient.get()
                .uri("https://api.a-bly.com/api/v2/anonymous/token/")
                .header("baggage", "ably-server=main")
                .header("x-isr-token-cache", "cache-repopulate:main")
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                .header("Accept", "application/json")
                .header("Referer", "https://a-bly.com/")
                .retrieve()
                .body(TokenResponse::class.java)
                ?: throw RuntimeException("Failed to get token response")
            
            val duration = System.currentTimeMillis() - startTime
            log.info("Successfully obtained fresh Ably token in {}ms", duration)
            log.debug("New token obtained (first 20 chars): {}...", response.token.take(20))
            
            response.token
            
        } catch (e: Exception) {
            val duration = System.currentTimeMillis() - startTime
            
            when {
                e.message?.contains("429") == true -> {
                    log.warn("Token API rate limit exceeded (429). Using cached token if available or extending cache time.")
                    
                    // 429 에러 시 기존 캐시된 토큰이 있으면 사용 (만료되어도)
                    cachedToken?.let { token ->
                        log.info("Using existing cached token due to rate limit ({}ms old)", System.currentTimeMillis() - tokenTime)
                        return token
                    }
                    
                    // 캐시된 토큰도 없으면 잠시 대기 후 재시도
                    log.warn("No cached token available. Waiting 5 seconds before retry...")
                    Thread.sleep(5000)
                    
                    try {
                        val retryResponse = restClient.get()
                            .uri("https://api.a-bly.com/api/v2/anonymous/token/")
                            .header("baggage", "ably-server=main")
                            .header("x-isr-token-cache", "cache-repopulate:main")
                            .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                            .header("Accept", "application/json")
                            .header("Referer", "https://a-bly.com/")
                            .retrieve()
                            .body(TokenResponse::class.java)
                            
                        log.info("Successfully obtained token on retry after rate limit")
                        retryResponse?.token ?: throw RuntimeException("Retry failed to get token response")
                        
                    } catch (retryException: Exception) {
                        log.error("Failed to get token even after retry. This will cause authentication failures.")
                        throw RuntimeException("Failed to refresh Ably token after rate limit", retryException)
                    }
                }
                else -> {
                    log.error("Failed to refresh Ably token after {}ms", duration, e)
                    throw RuntimeException("Failed to refresh Ably token", e)
                }
            }
        }
    }
    
    data class TokenResponse(
        val token: String
    )
}