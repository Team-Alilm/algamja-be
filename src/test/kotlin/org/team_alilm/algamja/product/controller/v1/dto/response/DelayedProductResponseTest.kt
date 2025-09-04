package org.team_alilm.algamja.product.controller.v1.dto.response

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.team_alilm.algamja.basket.entity.BasketRow
import org.team_alilm.algamja.common.enums.Store
import org.team_alilm.algamja.product.entity.ProductRow
import java.math.BigDecimal

class DelayedProductResponseTest {

    @Test
    fun `should calculate waiting days correctly`() {
        // Given
        val currentTime = System.currentTimeMillis()
        val threeDaysAgo = currentTime - (3 * 24 * 60 * 60 * 1000L) // 3 days ago

        val productRow = ProductRow(
            id = 1L,
            storeNumber = 12345L,
            name = "지연 상품",
            brand = "테스트 브랜드",
            thumbnailUrl = "https://example.com/delayed.jpg",
            store = Store.MUSINSA,
            firstCategory = "의류",
            secondCategory = "바지",
            price = BigDecimal("20000"),
            firstOption = "블랙",
            secondOption = "M",
            thirdOption = "",
            isAvailable = false,
            lastCheckedAt = null,
            isDelete = false,
            createdDate = currentTime,
            lastModifiedDate = currentTime
        )

        val basketRow = BasketRow(
            id = 1L,
            memberId = 1L,
            productId = 1L,
            isNotification = false,
            notificationDate = null,
            isHidden = false,
            isDelete = false,
            createdDate = threeDaysAgo,
            lastModifiedDate = threeDaysAgo
        )

        // When
        val result = DelayedProductResponse.from(productRow, basketRow)

        // Then
        assertEquals(1L, result.productId)
        assertEquals("지연 상품", result.name)
        assertEquals("테스트 브랜드", result.brand)
        assertEquals("https://example.com/delayed.jpg", result.thumbnailUrl)
        assertEquals("ABLY", result.store)
        assertEquals(3L, result.waitingDays)
        assertEquals(threeDaysAgo, result.addedDate)
    }

    @Test
    fun `should return zero waiting days for recently added product`() {
        // Given
        val currentTime = System.currentTimeMillis()
        val oneHourAgo = currentTime - (60 * 60 * 1000L) // 1 hour ago

        val productRow = ProductRow(
            id = 2L,
            storeNumber = 67890L,
            name = "최근 상품",
            brand = "신상 브랜드",
            thumbnailUrl = "https://example.com/recent.jpg",
            store = Store.MUSINSA,
            firstCategory = "액세서리",
            secondCategory = "가방",
            price = BigDecimal("30000"),
            firstOption = "브라운",
            secondOption = "",
            thirdOption = "",
            isDelete = false,
            createdDate = currentTime,
            lastModifiedDate = currentTime
        )

        val basketRow = BasketRow(
            id = 2L,
            memberId = 2L,
            productId = 2L,
            isNotification = false,
            notificationDate = null,
            isHidden = false,
            isDelete = false,
            createdDate = oneHourAgo,
            lastModifiedDate = oneHourAgo
        )

        // When
        val result = DelayedProductResponse.from(productRow, basketRow)

        // Then
        assertEquals(2L, result.productId)
        assertEquals("최근 상품", result.name)
        assertEquals(0L, result.waitingDays) // Should be 0 for same day
        assertEquals(oneHourAgo, result.addedDate)
    }

    @Test
    fun `should handle long waiting periods correctly`() {
        // Given
        val currentTime = System.currentTimeMillis()
        val thirtyDaysAgo = currentTime - (30 * 24 * 60 * 60 * 1000L) // 30 days ago

        val productRow = ProductRow(
            id = 3L,
            storeNumber = 99999L,
            name = "오래된 상품",
            brand = "빈티지 브랜드",
            thumbnailUrl = "https://example.com/vintage.jpg",
            store = Store.MUSINSA,
            firstCategory = "의류",
            secondCategory = "아우터",
            price = BigDecimal("50000"),
            firstOption = "네이비",
            secondOption = "XL",
            thirdOption = "롱",
            isDelete = false,
            createdDate = currentTime,
            lastModifiedDate = currentTime
        )

        val basketRow = BasketRow(
            id = 3L,
            memberId = 3L,
            productId = 3L,
            isNotification = false,
            notificationDate = null,
            isHidden = false,
            isDelete = false,
            createdDate = thirtyDaysAgo,
            lastModifiedDate = thirtyDaysAgo
        )

        // When
        val result = DelayedProductResponse.from(productRow, basketRow)

        // Then
        assertEquals(3L, result.productId)
        assertEquals("오래된 상품", result.name)
        assertEquals("빈티지 브랜드", result.brand)
        assertEquals("https://example.com/vintage.jpg", result.thumbnailUrl)
        assertEquals("ABLY", result.store)
        assertEquals(30L, result.waitingDays)
        assertEquals(thirtyDaysAgo, result.addedDate)
    }
}