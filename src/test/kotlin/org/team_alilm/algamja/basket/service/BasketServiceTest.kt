package org.team_alilm.algamja.basket.service

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.*
import org.team_alilm.algamja.basket.entity.BasketRow
import org.team_alilm.algamja.basket.repository.BasketExposedRepository
import org.team_alilm.algamja.common.enums.Store
import org.team_alilm.algamja.common.exception.BusinessException
import org.team_alilm.algamja.product.entity.ProductRow
import org.team_alilm.algamja.product.repository.ProductExposedRepository
import org.team_alilm.algamja.product.repository.projection.ProductWaitingCountProjection
import java.math.BigDecimal

class BasketServiceTest {

    private val basketExposedRepository = mock<BasketExposedRepository>()
    private val productExposedRepository = mock<ProductExposedRepository>()

    private val basketService = BasketService(basketExposedRepository, productExposedRepository)

    @Test
    fun `should return empty list when no basket items`() {
        // Given
        val memberId = 1L
        whenever(basketExposedRepository.fetchBasketsByMemberId(memberId)).thenReturn(emptyList())

        // When
        val result = basketService.getMyBasketProductList(memberId)

        // Then
        assertTrue(result.myBasketProductList.isEmpty())
    }

    @Test
    fun `should return basket products successfully`() {
        // Given
        val memberId = 1L
        val productId = 100L

        val mockBasketRow = BasketRow(
            id = 1L,
            memberId = memberId,
            productId = productId,
            isNotification = false,
            notificationDate = null,
            isHidden = false,
            isDelete = false,
            createdDate = System.currentTimeMillis(),
            lastModifiedDate = System.currentTimeMillis()
        )

        val mockProductRow = ProductRow(
            id = productId,
            storeNumber = 12345L,
            name = "장바구니 상품",
            brand = "테스트 브랜드",
            thumbnailUrl = "https://example.com/basket-item.jpg",
            store = Store.MUSINSA,
            firstCategory = "의류",
            secondCategory = "상의",
            price = BigDecimal("15000"),
            firstOption = "화이트",
            secondOption = "L",
            thirdOption = "",
            isAvailable = false,
            lastCheckedAt = null,
            isDelete = false,
            createdDate = System.currentTimeMillis(),
            lastModifiedDate = System.currentTimeMillis()
        )

        val mockWaitingCount = ProductWaitingCountProjection(productId, 3L)

        whenever(basketExposedRepository.fetchBasketsByMemberId(memberId)).thenReturn(listOf(mockBasketRow))
        whenever(productExposedRepository.fetchProductsByIds(listOf(productId))).thenReturn(listOf(mockProductRow))
        whenever(basketExposedRepository.fetchWaitingCounts(listOf(productId))).thenReturn(listOf(mockWaitingCount))

        // When
        val result = basketService.getMyBasketProductList(memberId)

        // Then
        assertEquals(1, result.myBasketProductList.size)
        
        val basketProduct = result.myBasketProductList.first()
        assertEquals(1L, basketProduct.basketId)
        assertEquals(productId, basketProduct.productId)
        assertEquals("장바구니 상품", basketProduct.name)
        assertEquals("테스트 브랜드", basketProduct.brand)
        assertEquals("MUSINSA", basketProduct.store)
        assertEquals(15000L, basketProduct.price)
        assertEquals(3L, basketProduct.waitingCount)
        assertEquals(false, basketProduct.notification)
    }

    @Test
    fun `should create basket successfully`() {
        // Given
        val memberId = 1L
        val productId = 200L
        val expectedBasketId = 10L

        whenever(basketExposedRepository.fetchBasketByMemberIdAndProductId(memberId, productId)).thenReturn(null)
        whenever(basketExposedRepository.createBasket(memberId, productId)).thenReturn(expectedBasketId)

        // When
        basketService.copyBasket(memberId, productId)

        // Then
        verify(basketExposedRepository).createBasket(memberId, productId)
    }

    @Test
    fun `should restore existing basket if already exists`() {
        // Given
        val memberId = 1L
        val productId = 200L
        val existingBasketId = 5L

        val existingBasketRow = BasketRow(
            id = existingBasketId,
            memberId = memberId,
            productId = productId,
            isNotification = false,
            notificationDate = null,
            isHidden = false,
            isDelete = true, // deleted basket
            createdDate = System.currentTimeMillis(),
            lastModifiedDate = System.currentTimeMillis()
        )

        whenever(basketExposedRepository.fetchBasketByMemberIdAndProductId(memberId, productId)).thenReturn(null)
        whenever(basketExposedRepository.fetchAnyBasketByMemberIdAndProductId(memberId, productId)).thenReturn(existingBasketRow)

        // When
        basketService.copyBasket(memberId, productId)

        // Then
        verify(basketExposedRepository).restoreBasket(existingBasketId)
        verify(basketExposedRepository, never()).createBasket(any(), any())
    }

    @Test
    fun `should do nothing when trying to add existing active basket`() {
        // Given
        val memberId = 1L
        val productId = 200L

        val activeBasketRow = BasketRow(
            id = 5L,
            memberId = memberId,
            productId = productId,
            isNotification = false,
            notificationDate = null,
            isHidden = false,
            isDelete = false, // active basket
            createdDate = System.currentTimeMillis(),
            lastModifiedDate = System.currentTimeMillis()
        )

        whenever(basketExposedRepository.fetchAnyBasketByMemberIdAndProductId(memberId, productId)).thenReturn(activeBasketRow)

        // When
        basketService.copyBasket(memberId, productId)

        // Then - should do nothing (idempotent behavior)
        verify(basketExposedRepository, never()).createBasket(any(), any())
        verify(basketExposedRepository, never()).restoreBasket(any())
    }

    @Test
    fun `should delete basket successfully`() {
        // Given
        val memberId = 1L
        val basketId = 10L

        val mockBasketRow = BasketRow(
            id = basketId,
            memberId = memberId,
            productId = 100L,
            isNotification = false,
            notificationDate = null,
            isHidden = false,
            isDelete = false,
            createdDate = System.currentTimeMillis(),
            lastModifiedDate = System.currentTimeMillis()
        )

        whenever(basketExposedRepository.fetchBasketById(basketId)).thenReturn(mockBasketRow)

        // When
        basketService.deleteBasket(memberId, basketId)

        // Then
        verify(basketExposedRepository).deleteBasket(basketId)
    }

    @Test
    fun `should throw exception when basket not found for delete`() {
        // Given
        val memberId = 1L
        val basketId = 999L

        whenever(basketExposedRepository.fetchBasketById(basketId)).thenReturn(null)

        // When & Then
        assertThrows<BusinessException> {
            basketService.deleteBasket(memberId, basketId)
        }
    }

    @Test
    fun `should throw exception when trying to delete other member's basket`() {
        // Given
        val memberId = 1L
        val otherMemberId = 2L
        val basketId = 10L

        val otherMemberBasket = BasketRow(
            id = basketId,
            memberId = otherMemberId, // different member
            productId = 100L,
            isNotification = false,
            notificationDate = null,
            isHidden = false,
            isDelete = false,
            createdDate = System.currentTimeMillis(),
            lastModifiedDate = System.currentTimeMillis()
        )

        whenever(basketExposedRepository.fetchBasketById(basketId)).thenReturn(otherMemberBasket)

        // When & Then
        assertThrows<BusinessException> {
            basketService.deleteBasket(memberId, basketId)
        }
    }
}