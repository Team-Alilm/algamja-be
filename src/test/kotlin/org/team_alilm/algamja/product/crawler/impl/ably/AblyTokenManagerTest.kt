package org.team_alilm.algamja.product.crawler.impl.ably

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.springframework.web.client.RestClient

class AblyTokenManagerTest {

    private val mockRestClient = mock<RestClient>()
    private val mockRequestHeadersUriSpec = mock<RestClient.RequestHeadersUriSpec<*>>()
    private val mockRequestHeadersSpec = mock<RestClient.RequestHeadersSpec<*>>()
    private val mockResponseSpec = mock<RestClient.ResponseSpec>()
    
    private val tokenManager = AblyTokenManager(mockRestClient)

    @Test
    fun `should get token successfully`() {
        // Given
        val expectedToken = "test-token-123"
        val tokenResponse = AblyTokenManager.TokenResponse(token = expectedToken)
        
        whenever(mockRestClient.get()).thenReturn(mockRequestHeadersUriSpec)
        whenever(mockRequestHeadersUriSpec.uri("https://api.a-bly.com/api/v2/anonymous/token/"))
            .thenReturn(mockRequestHeadersSpec)
        whenever(mockRequestHeadersSpec.header("baggage", "ably-server=main"))
            .thenReturn(mockRequestHeadersSpec)
        whenever(mockRequestHeadersSpec.header("x-isr-token-cache", "cache-repopulate:main"))
            .thenReturn(mockRequestHeadersSpec)
        whenever(mockRequestHeadersSpec.retrieve()).thenReturn(mockResponseSpec)
        whenever(mockResponseSpec.body(AblyTokenManager.TokenResponse::class.java))
            .thenReturn(tokenResponse)

        // When
        val result = tokenManager.getToken()

        // Then
        assertEquals(expectedToken, result)
    }

    @Test
    fun `should throw exception when token response is null`() {
        // Given
        whenever(mockRestClient.get()).thenReturn(mockRequestHeadersUriSpec)
        whenever(mockRequestHeadersUriSpec.uri("https://api.a-bly.com/api/v2/anonymous/token/"))
            .thenReturn(mockRequestHeadersSpec)
        whenever(mockRequestHeadersSpec.header("baggage", "ably-server=main"))
            .thenReturn(mockRequestHeadersSpec)
        whenever(mockRequestHeadersSpec.header("x-isr-token-cache", "cache-repopulate:main"))
            .thenReturn(mockRequestHeadersSpec)
        whenever(mockRequestHeadersSpec.retrieve()).thenReturn(mockResponseSpec)
        whenever(mockResponseSpec.body(AblyTokenManager.TokenResponse::class.java))
            .thenReturn(null)

        // When & Then
        assertThrows(RuntimeException::class.java) {
            tokenManager.getToken()
        }
    }
}