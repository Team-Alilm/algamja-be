package org.team_alilm.algamja.product.crawler.impl.ably

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.springframework.web.client.RestClient
import org.team_alilm.algamja.common.exception.BusinessException
import org.team_alilm.algamja.product.crawler.impl.ably.dto.AblyApiResponse
import org.team_alilm.algamja.product.crawler.impl.ably.dto.AblyCategory
import org.team_alilm.algamja.product.crawler.impl.ably.dto.AblyGoods
import org.team_alilm.algamja.product.crawler.impl.ably.dto.AblyMarket
import org.team_alilm.algamja.product.crawler.impl.ably.dto.AblyOptionComponent
import org.team_alilm.algamja.product.crawler.impl.ably.dto.AblyOptionsResponse
import org.team_alilm.algamja.product.crawler.impl.ably.dto.AblyPriceInfo
import java.math.BigDecimal

class AblyCrawlerTest {

    private val mockRestClient = mock<RestClient>()
    private val mockRequestHeadersUriSpec = mock<RestClient.RequestHeadersUriSpec<*>>()
    private val mockRequestHeadersSpec = mock<RestClient.RequestHeadersSpec<*>>()
    private val mockResponseSpec = mock<RestClient.ResponseSpec>()
    
    private val crawler = AblyCrawler(mockRestClient)

    @Test
    fun `should support ably URLs`() {
        // Given
        val validUrls = listOf(
            "https://m.a-bly.com/goods/51801138",
            "https://a-bly.com/goods/12345",
            "https://www.a-bly.com/goods/99999?param=test"
        )
        val invalidUrls = listOf(
            "https://example.com/goods/123",
            "https://musinsa.com/goods/123",
            "https://a-bly.com/invalid/123",
            "invalid-url"
        )

        // When & Then
        validUrls.forEach { url ->
            assertTrue(crawler.supports(url), "Should support URL: $url")
        }
        
        invalidUrls.forEach { url ->
            assertFalse(crawler.supports(url), "Should not support URL: $url")
        }
    }

    @Test
    fun `should normalize URLs correctly`() {
        // When & Then
        assertEquals(
            "https://a-bly.com/goods/51801138",
            crawler.normalize("https://m.a-bly.com/goods/51801138?param=test")
        )
        
        assertEquals(
            "https://a-bly.com/goods/12345",
            crawler.normalize("https://a-bly.com/goods/12345")
        )
    }

    @Test
    fun `should fetch product successfully`() {
        // Given
        val testUrl = "https://m.a-bly.com/goods/51801138"
        val testToken = "test-token"
        
        val mockMarket = AblyMarket(sno = 10071, name = "조이조이")
        val mockPriceInfo = AblyPriceInfo(consumer = 40000, thumbnailPrice = 18420)
        val mockCategory = AblyCategory(sno = 176, name = "롱팬츠", depth = 1)
        val mockGoods = AblyGoods(
            sno = 51801138,
            name = "벤티 벌룬핏 핀턱 밴딩 데일리 면바지",
            market = mockMarket,
            priceInfo = mockPriceInfo,
            coverImages = listOf("https://example.com/image1.jpg", "https://example.com/image2.jpg"),
            displayCategories = listOf(mockCategory)
        )
        val mockResponse = AblyApiResponse(goods = mockGoods)
        
        val mockOptionComponent = AblyOptionComponent(
            sno = 1394270513,
            depth = 1,
            name = "[PT385] 벤티 핀턱 밴딩 팬츠_화이트",
            isFinalDepth = false,
            goodsOptionSno = 394270513
        )
        val mockOptionsResponse = AblyOptionsResponse(
            name = "색상",
            optionComponents = listOf(mockOptionComponent)
        )

        // Token is now hardcoded, no need to mock
        whenever(mockRestClient.get()).thenReturn(mockRequestHeadersUriSpec)
        whenever(mockRequestHeadersUriSpec.uri(any<String>())).thenReturn(mockRequestHeadersSpec)
        whenever(mockRequestHeadersSpec.header(any<String>(), any<String>())).thenReturn(mockRequestHeadersSpec)
        whenever(mockRequestHeadersSpec.retrieve()).thenReturn(mockResponseSpec)
        whenever(mockResponseSpec.body(AblyApiResponse::class.java)).thenReturn(mockResponse)
        whenever(mockResponseSpec.body(AblyOptionsResponse::class.java)).thenReturn(mockOptionsResponse)

        // When
        val result = crawler.fetch(testUrl)

        // Then
        assertEquals(51801138, result.storeNumber)
        assertEquals("벤티 벌룬핏 핀턱 밴딩 데일리 면바지", result.name)
        assertEquals("조이조이", result.brand)
        assertEquals("ABLY", result.store)
        assertEquals(BigDecimal.valueOf(18420), result.price)
        assertEquals("바지", result.firstCategory)
        assertEquals("롱팬츠", result.secondCategory)
        assertEquals("https://example.com/image1.jpg", result.thumbnailUrl)
        assertEquals(2, result.imageUrls.size)
        assertEquals(listOf("화이트"), result.firstOptions)
        assertTrue(result.secondOptions.isEmpty())
        assertTrue(result.thirdOptions.isEmpty())
    }

    @Test
    fun `should handle null goods response`() {
        // Given
        val testUrl = "https://m.a-bly.com/goods/51801138"
        val testToken = "test-token"
        val mockResponse = AblyApiResponse(goods = null)

        // Token is now hardcoded, no need to mock
        whenever(mockRestClient.get()).thenReturn(mockRequestHeadersUriSpec)
        whenever(mockRequestHeadersUriSpec.uri(any<String>())).thenReturn(mockRequestHeadersSpec)
        whenever(mockRequestHeadersSpec.header(any<String>(), any<String>())).thenReturn(mockRequestHeadersSpec)
        whenever(mockRequestHeadersSpec.retrieve()).thenReturn(mockResponseSpec)
        whenever(mockResponseSpec.body(AblyApiResponse::class.java)).thenReturn(mockResponse)

        // When & Then
        assertThrows<BusinessException> {
            crawler.fetch(testUrl)
        }
    }

    @Test
    fun `should handle invalid URL format`() {
        // Given
        val invalidUrl = "https://a-bly.com/invalid/path"

        // When & Then
        assertThrows<BusinessException> {
            crawler.fetch(invalidUrl)
        }
    }
}