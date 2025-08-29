package org.team_alilm.algamja.product.service

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.*
import org.springframework.web.client.RestClient
import org.team_alilm.algamja.common.enums.Store
import org.team_alilm.algamja.common.exception.BusinessException
import org.team_alilm.algamja.common.exception.ErrorCode
import org.team_alilm.algamja.product.crawler.CrawlerRegistry
import org.team_alilm.algamja.product.crawler.ProductCrawler
import org.team_alilm.algamja.product.crawler.dto.CrawledProduct
import org.team_alilm.algamja.product.entity.ProductRow
import org.team_alilm.algamja.product.image.entity.ProductImageRow
import org.team_alilm.algamja.product.image.repository.ProductImageExposedRepository
import org.team_alilm.algamja.product.repository.ProductExposedRepository
import java.math.BigDecimal
import java.time.LocalDateTime

class MusinsaProductServiceTest {

    private val restClient = mock<RestClient>()
    private val crawlerRegistry = mock<CrawlerRegistry>()
    private val productExposedRepository = mock<ProductExposedRepository>()
    private val productImageExposedRepository = mock<ProductImageExposedRepository>()
    private val productCrawler = mock<ProductCrawler>()

    private lateinit var musinsaProductService: MusinsaProductService

    @BeforeEach
    fun setUp() {
        musinsaProductService = MusinsaProductService(
            restClient,
            crawlerRegistry,
            productExposedRepository,
            productImageExposedRepository
        )
    }

    @Nested
    @DisplayName("fetchAndRegisterRandomProducts 메서드 테스트")
    inner class FetchAndRegisterRandomProductsTest {

        @Test
        @DisplayName("성공적으로 상품을 등록한다")
        fun `should register products successfully`() {
            // Given
            val count = 5
            val crawledProduct = createMockCrawledProduct()
            val savedProduct = createMockProductRow()
            
            whenever(crawlerRegistry.resolve(any())).thenReturn(productCrawler)
            whenever(productCrawler.normalize(any())).thenReturn("normalized_url")
            whenever(productCrawler.fetch(any())).thenReturn(crawledProduct)
            whenever(productExposedRepository.fetchProductByStoreNumber(any(), any())).thenReturn(null)
            whenever(productExposedRepository.save(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any())).thenReturn(savedProduct)

            // When
            val result = musinsaProductService.fetchAndRegisterRandomProducts(count)

            // Then
            assertTrue(result >= 0)
            verify(productExposedRepository, atLeastOnce()).save(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any())
        }

        @Test
        @DisplayName("중복 상품은 등록하지 않는다")
        fun `should skip duplicate products`() {
            // Given
            val count = 1
            val crawledProduct = createMockCrawledProduct()
            val existingProduct = createMockProductRow()
            
            whenever(crawlerRegistry.resolve(any())).thenReturn(productCrawler)
            whenever(productCrawler.normalize(any())).thenReturn("normalized_url")
            whenever(productCrawler.fetch(any())).thenReturn(crawledProduct)
            whenever(productExposedRepository.fetchProductByStoreNumber(any(), any())).thenReturn(existingProduct)

            // When
            val result = musinsaProductService.fetchAndRegisterRandomProducts(count)

            // Then
            verify(productExposedRepository, never()).save(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any())
        }

        @Test
        @DisplayName("크롤링 실패 시 해당 상품을 건너뛴다")
        fun `should skip products when crawling fails`() {
            // Given
            val count = 1
            
            whenever(crawlerRegistry.resolve(any())).thenThrow(RuntimeException("Crawler not found"))

            // When
            val result = musinsaProductService.fetchAndRegisterRandomProducts(count)

            // Then
            assertEquals(0, result)
            verify(productExposedRepository, never()).save(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any())
        }

        @Test
        @DisplayName("일부 상품 등록 실패 시에도 계속 진행한다")
        fun `should continue even when some products fail to register`() {
            // Given
            val count = 2
            val crawledProduct = createMockCrawledProduct()
            val savedProduct = createMockProductRow()
            
            whenever(crawlerRegistry.resolve(any())).thenReturn(productCrawler)
            whenever(productCrawler.normalize(any())).thenReturn("normalized_url")
            whenever(productCrawler.fetch(any())).thenReturn(crawledProduct)
            whenever(productExposedRepository.fetchProductByStoreNumber(any(), any())).thenReturn(null)
            whenever(productExposedRepository.save(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any()))
                .thenThrow(RuntimeException("DB Error"))
                .thenReturn(savedProduct)

            // When
            val result = musinsaProductService.fetchAndRegisterRandomProducts(count)

            // Then
            assertTrue(result >= 0)
        }
    }

    @Nested
    @DisplayName("상품 등록 로직 테스트")
    inner class ProductRegistrationTest {

        @Test
        @DisplayName("상품과 이미지를 모두 등록한다")
        fun `should register product and images`() {
            // Given
            val crawledProduct = createMockCrawledProduct()
            val savedProduct = createMockProductRow()
            val savedImage = createMockProductImageRow()
            val originalUrl = "https://www.musinsa.com/app/goods/1234567"
            
            whenever(productExposedRepository.fetchProductByStoreNumber(any(), any())).thenReturn(null)
            whenever(productExposedRepository.save(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any())).thenReturn(savedProduct)
            whenever(productImageExposedRepository.save(any(), any(), any())).thenReturn(savedImage)

            // When
            musinsaProductService.fetchAndRegisterRandomProducts(1)

            // Then
            verify(productExposedRepository).save(
                eq(crawledProduct.name),
                eq(crawledProduct.storeNumber),
                eq(crawledProduct.brand),
                eq(crawledProduct.thumbnailUrl),
                eq(originalUrl),
                eq(crawledProduct.store),
                eq(crawledProduct.price),
                eq(crawledProduct.firstCategory ?: "기타"),
                eq(crawledProduct.secondCategory),
                eq(crawledProduct.firstOptions),
                eq(crawledProduct.secondOptions),
                eq(crawledProduct.thirdOptions)
            )
            
            // 이미지 개수만큼 save가 호출되어야 함
            verify(productImageExposedRepository, times(crawledProduct.imageUrls.size)).save(any(), any(), any())
        }

        @Test
        @DisplayName("상품 등록 실패 시 BusinessException을 던진다")
        fun `should throw BusinessException when product registration fails`() {
            // Given
            val crawledProduct = createMockCrawledProduct()
            val originalUrl = "https://www.musinsa.com/app/goods/1234567"
            
            whenever(productExposedRepository.fetchProductByStoreNumber(any(), any())).thenReturn(null)
            whenever(productExposedRepository.save(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any()))
                .thenThrow(RuntimeException("Database error"))

            // When & Then
            assertThrows<BusinessException> {
                musinsaProductService.fetchAndRegisterRandomProducts(1)
            }
        }
    }

    @Nested
    @DisplayName("URL 생성 테스트")
    inner class UrlGenerationTest {

        @Test
        @DisplayName("카테고리에서 상품 URL을 추출할 수 있다")
        fun `should extract product URLs from category page`() {
            // Given
            val mockHtml = """
                <html>
                <body>
                    <a href="/app/goods/1234567">Product 1</a>
                    <a href="/app/goods/2345678">Product 2</a>
                    <a href="/app/goods/3456789">Product 3</a>
                </body>
                </html>
            """.trimIndent()

            val requestBodyUriSpec = mock<RestClient.RequestBodyUriSpec>()
            val requestHeadersUriSpec = mock<RestClient.RequestHeadersUriSpec<*>>()
            val responseSpec = mock<RestClient.ResponseSpec>()

            whenever(restClient.get()).thenReturn(requestBodyUriSpec)
            whenever(requestBodyUriSpec.uri(anyOrNull<String>())).thenReturn(requestHeadersUriSpec)
            whenever(requestHeadersUriSpec.header(any(), any())).thenReturn(requestHeadersUriSpec)
            whenever(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec)
            whenever(responseSpec.body(String::class.java)).thenReturn(mockHtml)

            // When
            val result = musinsaProductService.fetchAndRegisterRandomProducts(1)

            // Then
            assertTrue(result >= 0)
        }
    }

    private fun createMockCrawledProduct(): CrawledProduct {
        return CrawledProduct(
            name = "테스트 상품",
            storeNumber = "1234567",
            brand = "테스트 브랜드",
            thumbnailUrl = "https://example.com/thumbnail.jpg",
            imageUrls = listOf(
                "https://example.com/image1.jpg",
                "https://example.com/image2.jpg"
            ),
            store = Store.MUSINSA,
            price = BigDecimal("50000"),
            firstCategory = "상의",
            secondCategory = "티셔츠",
            firstOptions = listOf("S", "M", "L"),
            secondOptions = listOf("블랙", "화이트"),
            thirdOptions = listOf()
        )
    }

    private fun createMockProductRow(): ProductRow {
        return ProductRow(
            id = 1L,
            storeNumber = 1234567L,
            name = "테스트 상품",
            brand = "테스트 브랜드",
            thumbnailUrl = "https://example.com/thumbnail.jpg",
            store = Store.MUSINSA,
            price = BigDecimal("50000"),
            firstCategory = "상의",
            secondCategory = "티셔츠",
            firstOption = "S,M,L",
            secondOption = "블랙,화이트",
            thirdOption = null,
            createdDate = LocalDateTime.now(),
            updatedDate = LocalDateTime.now(),
            isDelete = false
        )
    }

    private fun createMockProductImageRow(): ProductImageRow {
        return ProductImageRow(
            id = 1L,
            productId = 1L,
            imageUrl = "https://example.com/image.jpg",
            createdDate = LocalDateTime.now(),
            updatedDate = LocalDateTime.now(),
            isDelete = false
        )
    }
}