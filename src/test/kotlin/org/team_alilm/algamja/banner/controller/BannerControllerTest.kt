package org.team_alilm.algamja.banner.controller

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.team_alilm.algamja.banner.controller.dto.response.BannerListResponse
import org.team_alilm.algamja.banner.controller.dto.response.BannerResponse
import org.team_alilm.algamja.banner.service.BannerService
import org.team_alilm.algamja.common.security.jwt.JwtFilter
import org.team_alilm.algamja.common.security.CustomUserDetailsService

@WebMvcTest(BannerController::class)
class BannerControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockBean
    private lateinit var bannerService: BannerService

    @MockBean
    private lateinit var jwtFilter: JwtFilter

    @MockBean
    private lateinit var customUserDetailsService: CustomUserDetailsService

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

        // When & Then
        mockMvc.perform(get("/api/v1/banners"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.banners").isArray)
            .andExpect(jsonPath("$.data.banners.length()").value(2))
            .andExpect(jsonPath("$.data.banners[0].id").value(1))
            .andExpect(jsonPath("$.data.banners[0].title").value("메인 배너"))
            .andExpect(jsonPath("$.data.banners[0].imageUrl").value("https://example.com/banner1.jpg"))
            .andExpect(jsonPath("$.data.banners[0].clickUrl").value("https://example.com/event"))
            .andExpect(jsonPath("$.data.banners[0].priority").value(100))
            .andExpect(jsonPath("$.data.banners[1].id").value(2))
            .andExpect(jsonPath("$.data.banners[1].title").value("할인 배너"))
            .andExpect(jsonPath("$.data.banners[1].imageUrl").value("https://example.com/banner2.jpg"))
            .andExpect(jsonPath("$.data.banners[1].clickUrl").isEmpty())
            .andExpect(jsonPath("$.data.banners[1].priority").value(50))

        verify(bannerService).getActiveBanners()
    }

    @Test
    fun `should return empty banner list when no active banners`() {
        // Given
        val emptyBannerListResponse = BannerListResponse(emptyList())
        whenever(bannerService.getActiveBanners()).thenReturn(emptyBannerListResponse)

        // When & Then
        mockMvc.perform(get("/api/v1/banners"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.banners").isArray)
            .andExpect(jsonPath("$.data.banners.length()").value(0))

        verify(bannerService).getActiveBanners()
    }
}