package org.team_alilm.algamja.product.crawler.impl.ably

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

@Component
class AblyTokenManager(
    private val restClient: RestClient
) {
    
    private val log = LoggerFactory.getLogger(javaClass)
    
    private var cachedToken: String? = null
    private var tokenExpiry: LocalDateTime? = null
    
    fun getToken(): String {
        if (isTokenValid()) {
            log.debug("Using cached Ably token (expires at: {})", tokenExpiry)
            return cachedToken!!
        }
        
        log.info("Ably token expired or not found, refreshing token...")
        return refreshToken()
    }
    
    /**
     * 403 에러 등으로 토큰이 유효하지 않을 때 강제 갱신
     */
    fun forceRefreshToken(): String {
        log.info("Force refreshing Ably token due to authentication error")
        cachedToken = null
        tokenExpiry = null
        return refreshToken()
    }
    
    private fun isTokenValid(): Boolean {
        val isValid = cachedToken != null && 
               tokenExpiry != null && 
               LocalDateTime.now().isBefore(tokenExpiry)
        
        log.trace("Token validity check: valid={}, expiry={}", isValid, tokenExpiry)
        return isValid
    }
    
    private fun refreshToken(): String {
        val startTime = System.currentTimeMillis()
        
        try {
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
            
            cachedToken = response.token
            tokenExpiry = LocalDateTime.now().plus(50, ChronoUnit.MINUTES)
            
            val duration = System.currentTimeMillis() - startTime
            log.info("Successfully refreshed Ably token in {}ms, expires at: {}", duration, tokenExpiry)
            log.info("New token obtained (first 20 chars): {}...", cachedToken!!.take(20))
            
            return cachedToken!!
        } catch (e: Exception) {
            val duration = System.currentTimeMillis() - startTime
            log.error("Failed to refresh Ably token after {}ms", duration, e)
            throw RuntimeException("Failed to refresh Ably token", e)
        }
    }
    
    data class TokenResponse(
        val token: String
    )
}