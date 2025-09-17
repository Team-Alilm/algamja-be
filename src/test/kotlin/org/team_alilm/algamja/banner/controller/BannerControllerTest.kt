package org.team_alilm.algamja.banner.controller

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.mockito.kotlin.*
import org.team_alilm.algamja.banner.controller.dto.response.BannerListResponse
import org.team_alilm.algamja.banner.controller.dto.response.BannerResponse
import org.team_alilm.algamja.banner.service.BannerService

class BannerControllerTest {

    private val bannerService = mock<BannerService>()
    private val bannerController = BannerController(bannerService)

    @Test
    fun `should return active banners successfully`() {
        // Given
        val bannerResponse1 = BannerResponse(
            id = 1L,
            title = "메인 배너",
            imageUrl = "https://example.com/banner1.jpg",
            clickUrl = "https://example.com/event",
            priority = 100
        )

        val bannerResponse2 = BannerResponse(
            id = 2L,
            title = "할인 배너",
            imageUrl = "https://example.com/banner2.jpg",
            clickUrl = null,
            priority = 50
        )

        val bannerListResponse = BannerListResponse(listOf(bannerResponse1, bannerResponse2))
        whenever(bannerService.getActiveBanners()).thenReturn(bannerListResponse)

        // When
        val result = bannerController.getActiveBanners()

        // Then
        assertNotNull(result)
        assertEquals("0000", result.code)
        assertEquals(2, result.data!!.banners.size)

        val firstBanner = result.data!!.banners[0]
        assertEquals(1L, firstBanner.id)
        assertEquals("메인 배너", firstBanner.title)
        assertEquals("https://example.com/banner1.jpg", firstBanner.imageUrl)
        assertEquals("https://example.com/event", firstBanner.clickUrl)
        assertEquals(100, firstBanner.priority)

        val secondBanner = result.data!!.banners[1]
        assertEquals(2L, secondBanner.id)
        assertEquals("할인 배너", secondBanner.title)
        assertEquals("https://example.com/banner2.jpg", secondBanner.imageUrl)
        assertNull(secondBanner.clickUrl)
        assertEquals(50, secondBanner.priority)

        verify(bannerService).getActiveBanners()
    }

    @Test
    fun `should return empty banner list when no active banners`() {
        // Given
        val emptyBannerListResponse = BannerListResponse(emptyList())
        whenever(bannerService.getActiveBanners()).thenReturn(emptyBannerListResponse)

        // When
        val result = bannerController.getActiveBanners()

        // Then
        assertNotNull(result)
        assertEquals("0000", result.code)
        assertTrue(result.data!!.banners.isEmpty())

        verify(bannerService).getActiveBanners()
    }
}