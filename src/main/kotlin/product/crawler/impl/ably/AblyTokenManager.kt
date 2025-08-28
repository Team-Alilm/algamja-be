package org.team_alilm.product.crawler.impl.ably

import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

@Component
class AblyTokenManager(
    private val restClient: RestClient
) {
    
    private var cachedToken: String? = null
    private var tokenExpiry: LocalDateTime? = null
    
    fun getToken(): String {
        if (isTokenValid()) {
            return cachedToken!!
        }
        
        return refreshToken()
    }
    
    private fun isTokenValid(): Boolean {
        return cachedToken != null && 
               tokenExpiry != null && 
               LocalDateTime.now().isBefore(tokenExpiry)
    }
    
    private fun refreshToken(): String {
        try {
            val response = restClient.get()
                .uri("https://api.a-bly.com/api/v2/anonymous/token/")
                .header("baggage", "ably-server=main")
                .header("x-isr-token-cache", "cache-repopulate:main")
                .retrieve()
                .body(TokenResponse::class.java)
                ?: throw RuntimeException("Failed to get token response")
            
            cachedToken = response.token
            tokenExpiry = LocalDateTime.now().plus(50, ChronoUnit.MINUTES)
            
            return cachedToken!!
        } catch (e: Exception) {
            throw RuntimeException("Failed to refresh Ably token", e)
        }
    }
    
    data class TokenResponse(
        val token: String
    )
}