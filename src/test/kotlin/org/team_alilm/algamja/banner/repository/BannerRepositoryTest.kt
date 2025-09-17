package org.team_alilm.algamja.banner.repository

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.mockito.kotlin.*
import org.team_alilm.algamja.banner.entity.BannerRow
import org.team_alilm.algamja.banner.entity.BannerTable

class BannerRepositoryTest {

    private val bannerRepository = BannerRepository()

    @Test
    fun `create should accept all parameters correctly`() {
        // Given
        val title = "테스트 배너"
        val imageUrl = "https://example.com/banner.jpg"
        val clickUrl = "https://example.com/click"
        val priority = 100
        val startDate = System.currentTimeMillis()
        val endDate = startDate + 86400000L
        val isActive = true

        // When & Then - 파라미터 검증만 수행 (실제 DB 연동 없이)
        assertDoesNotThrow {
            // 메서드 시그니처 검증
            val method = bannerRepository::class.java.getDeclaredMethod(
                "create",
                String::class.java,
                String::class.java,
                String::class.java,
                Int::class.java,
                Long::class.java,
                Long::class.java,
                Boolean::class.java
            )
            assertNotNull(method)
        }
    }

    @Test
    fun `update should accept all parameters correctly`() {
        // Given
        val id = 1L
        val title = "수정된 배너"
        val imageUrl = "https://example.com/updated-banner.jpg"
        val clickUrl = "https://example.com/updated-click"
        val priority = 50
        val startDate = System.currentTimeMillis()
        val endDate = startDate + 86400000L
        val isActive = false

        // When & Then - 파라미터 검증만 수행
        assertDoesNotThrow {
            val method = bannerRepository::class.java.getDeclaredMethod(
                "update",
                Long::class.java,
                String::class.java,
                String::class.java,
                String::class.java,
                Int::class.java,
                Long::class.java,
                Long::class.java,
                Boolean::class.java
            )
            assertNotNull(method)
        }
    }

    @Test
    fun `repository should have required methods`() {
        // When & Then - 메서드 존재 검증
        assertDoesNotThrow {
            bannerRepository::class.java.getDeclaredMethod("fetchActiveBanners")
            bannerRepository::class.java.getDeclaredMethod("fetchById", Long::class.java)
            bannerRepository::class.java.getDeclaredMethod("fetchAll")
            bannerRepository::class.java.getDeclaredMethod("softDelete", Long::class.java)
            bannerRepository::class.java.getDeclaredMethod("updateActiveStatus", Long::class.java, Boolean::class.java)
        }
    }
}