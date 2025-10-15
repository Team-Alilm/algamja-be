package org.team_alilm.algamja.product.service

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.*
import org.team_alilm.algamja.basket.entity.BasketRow
import org.team_alilm.algamja.basket.repository.BasketExposedRepository
import org.team_alilm.algamja.common.enums.Sort
import org.team_alilm.algamja.common.enums.Store
import org.team_alilm.algamja.common.exception.BusinessException
import org.team_alilm.algamja.product.controller.v1.dto.param.ProductListParam
import org.team_alilm.algamja.product.controller.v1.dto.param.ProductCountParam
import org.team_alilm.algamja.product.crawler.CrawlerRegistry
import org.team_alilm.algamja.product.entity.ProductRow
import org.team_alilm.algamja.product.image.repository.ProductImageExposedRepository
import org.team_alilm.algamja.product.repository.ProductExposedRepository
import java.math.BigDecimal

class ProductServiceTest {

    private val productExposedRepository = mock<ProductExposedRepository>()
    private val basketExposedRepository = mock<BasketExposedRepository>()
    private val productImageExposedRepository = mock<ProductImageExposedRepository>()
    private val crawlerRegistry = mock<CrawlerRegistry>()

    private val productService = ProductService(
        productExposedRepository,
        basketExposedRepository,
        productImageExposedRepository,
        crawlerRegistry
    )

    @Test
    fun `should return product detail successfully`() {
        // Given
        val productId = 1L
        val mockProduct = ProductRow(
            id = productId,
            storeNumber = 12345L,
            name = "테스트 상품",
            brand = "테스트 브랜드",
            thumbnailUrl = "https://example.com/image.jpg",
            store = Store.MUSINSA,
            firstCategory = "의류",
            secondCategory = "상의",
            price = BigDecimal("10000"),
            firstOption = "블랙",
            secondOption = "L",
            thirdOption = "",
            isAvailable = false,
            lastCheckedAt = null,
            isDelete = false,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )

        whenever(productExposedRepository.fetchProductById(productId)).thenReturn(mockProduct)
        whenever(productImageExposedRepository.fetchProductImageById(productId)).thenReturn(emptyList())
        whenever(basketExposedRepository.fetchWaitingCount(productId)).thenReturn(5L)

        // When
        val result = productService.getProductDetail(productId)

        // Then
        assertNotNull(result)
        assertEquals(productId, result.id)
        assertEquals("테스트 상품", result.name)
        assertEquals("테스트 브랜드", result.brand)
        assertEquals("MUSINSA", result.store)
        assertEquals(10000L, result.price)
        assertEquals(5L, result.waitingCount)
    }

    @Test
    fun `should throw exception when product not found`() {
        // Given
        val productId = 999L
        whenever(productExposedRepository.fetchProductById(productId)).thenReturn(null)

        // When & Then
        assertThrows<BusinessException> {
            productService.getProductDetail(productId)
        }
    }

    @Test
    fun `should return most delayed product for member`() {
        // Given
        val memberId = 1L
        val productId = 100L
        val basketCreatedTime = System.currentTimeMillis() - (7 * 24 * 60 * 60 * 1000) // 7 days ago

        val mockBasketRow = BasketRow(
            id = 1L,
            memberId = memberId,
            productId = productId,
            isNotification = false,
            notificationDate = null,
            isHidden = false,
            isDelete = false,
            createdAt = basketCreatedTime,
            updatedAt = basketCreatedTime
        )

        val mockProductRow = ProductRow(
            id = productId,
            storeNumber = 12345L,
            name = "오래 기다린 상품",
            brand = "테스트 브랜드",
            thumbnailUrl = "https://example.com/delayed.jpg",
            store = Store.MUSINSA,
            firstCategory = "의류",
            secondCategory = "바지",
            price = BigDecimal("25000"),
            firstOption = "네이비",
            secondOption = "M",
            thirdOption = "",
            isAvailable = false,
            lastCheckedAt = null,
            isDelete = false,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )

        whenever(basketExposedRepository.fetchOldestWaitingProductByMember(memberId)).thenReturn(mockBasketRow)
        whenever(productExposedRepository.fetchProductById(productId)).thenReturn(mockProductRow)

        // When
        val result = productService.getMostDelayedProductByMember(memberId)

        // Then
        assertNotNull(result)
        assertEquals(productId, result!!.productId)
        assertEquals("오래 기다린 상품", result.name)
        assertEquals("테스트 브랜드", result.brand)
        assertEquals("MUSINSA", result.store)
        assertEquals(7L, result.waitingDays)
        assertEquals(basketCreatedTime, result.addedDate)
    }

    @Test
    fun `should return null when no waiting products for member`() {
        // Given
        val memberId = 1L
        whenever(basketExposedRepository.fetchOldestWaitingProductByMember(memberId)).thenReturn(null)

        // When
        val result = productService.getMostDelayedProductByMember(memberId)

        // Then
        assertNull(result)
    }

    @Test
    fun `should return null when product not found for delayed query`() {
        // Given
        val memberId = 1L
        val productId = 999L
        
        val mockBasketRow = BasketRow(
            id = 1L,
            memberId = memberId,
            productId = productId,
            isNotification = false,
            notificationDate = null,
            isHidden = false,
            isDelete = false,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )

        whenever(basketExposedRepository.fetchOldestWaitingProductByMember(memberId)).thenReturn(mockBasketRow)
        whenever(productExposedRepository.fetchProductById(productId)).thenReturn(null)

        // When
        val result = productService.getMostDelayedProductByMember(memberId)

        // Then
        assertNull(result)
    }

    @Test
    fun `should return product count successfully`() {
        // Given
        val param = ProductCountParam(
            keyword = null,
            category = null
        )
        val expectedCount = 42L

        whenever(productExposedRepository.countProducts(param)).thenReturn(expectedCount)

        // When
        val result = productService.getProductCount(param)

        // Then
        assertEquals(expectedCount, result.productCount)
    }
}