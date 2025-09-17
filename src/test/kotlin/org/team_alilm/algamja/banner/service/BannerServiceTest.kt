package org.team_alilm.algamja.banner.service

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.mockito.kotlin.*
import org.team_alilm.algamja.banner.entity.BannerRow
import org.team_alilm.algamja.banner.repository.BannerRepository
import org.jetbrains.exposed.dao.id.EntityID

class BannerServiceTest {

    private val bannerRepository = mock<BannerRepository>()
    private val bannerService = BannerService(bannerRepository)

    @Test
    fun `should return empty list when no active banners`() {
        // Given
        whenever(bannerRepository.fetchActiveBanners()).thenReturn(emptyList())

        // When
        val result = bannerService.getActiveBanners()

        // Then
        assertTrue(result.banners.isEmpty())
    }

    @Test
    fun `should return active banners successfully`() {
        // Given
        val mockBanner1 = mock<BannerRow> {
            on { id } doReturn EntityID(1L, org.team_alilm.algamja.banner.entity.BannerTable)
            on { title } doReturn "첫 번째 배너"
            on { imageUrl } doReturn "https://example.com/banner1.jpg"
            on { clickUrl } doReturn "https://example.com/click1"
            on { priority } doReturn 100
        }

        val mockBanner2 = mock<BannerRow> {
            on { id } doReturn EntityID(2L, org.team_alilm.algamja.banner.entity.BannerTable)
            on { title } doReturn "두 번째 배너"
            on { imageUrl } doReturn "https://example.com/banner2.jpg"
            on { clickUrl } doReturn null
            on { priority } doReturn 50
        }

        whenever(bannerRepository.fetchActiveBanners()).thenReturn(listOf(mockBanner1, mockBanner2))

        // When
        val result = bannerService.getActiveBanners()

        // Then
        assertEquals(2, result.banners.size)

        val firstBanner = result.banners[0]
        assertEquals(1L, firstBanner.id)
        assertEquals("첫 번째 배너", firstBanner.title)
        assertEquals("https://example.com/banner1.jpg", firstBanner.imageUrl)
        assertEquals("https://example.com/click1", firstBanner.clickUrl)
        assertEquals(100, firstBanner.priority)

        val secondBanner = result.banners[1]
        assertEquals(2L, secondBanner.id)
        assertEquals("두 번째 배너", secondBanner.title)
        assertEquals("https://example.com/banner2.jpg", secondBanner.imageUrl)
        assertNull(secondBanner.clickUrl)
        assertEquals(50, secondBanner.priority)
    }

    @Test
    fun `should call repository fetchActiveBanners method`() {
        // Given
        whenever(bannerRepository.fetchActiveBanners()).thenReturn(emptyList())

        // When
        bannerService.getActiveBanners()

        // Then
        verify(bannerRepository).fetchActiveBanners()
    }
}