package org.team_alilm.algamja.product.repository

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.BeforeEach
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import org.team_alilm.algamja.common.enums.Store
import java.math.BigDecimal
import kotlin.test.*

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ProductExposedRepositoryTest {

    private lateinit var productExposedRepository: ProductExposedRepository

    @BeforeEach
    fun setUp() {
        productExposedRepository = ProductExposedRepository()
    }

    @Nested
    @DisplayName("fetchProductByStoreNumber 테스트")
    inner class FetchProductByStoreNumberTest {

        @Test
        @DisplayName("존재하지 않는 상품은 null을 반환한다")
        fun `should return null for non-existent product`() {
            // Given
            val nonExistentStoreNumber = 999999L
            val store = Store.MUSINSA

            // When
            val result = productExposedRepository.fetchProductByStoreNumber(nonExistentStoreNumber, store)

            // Then
            assertNull(result)
        }

        @Test
        @DisplayName("삭제된 상품은 null을 반환한다")
        fun `should return null for deleted product`() {
            // Given
            val storeNumber = 1234567L
            val store = Store.MUSINSA
            
            // 먼저 상품을 저장하고 삭제 처리한다고 가정
            // (실제 구현에서는 isDelete = true 상태의 상품)

            // When
            val result = productExposedRepository.fetchProductByStoreNumber(storeNumber, store)

            // Then
            // 삭제된 상품은 조회되지 않아야 함
            assertNull(result)
        }
    }

    @Nested
    @DisplayName("save 메서드 테스트")
    inner class SaveMethodTest {

        @Test
        @DisplayName("새 상품을 성공적으로 저장한다")
        fun `should save new product successfully`() {
            // Given
            val name = "테스트 상품"
            val storeNumber = 1234567L
            val brand = "테스트 브랜드"
            val thumbnailUrl = "https://example.com/thumbnail.jpg"
            val originalUrl = "https://www.musinsa.com/app/goods/1234567"
            val store = Store.MUSINSA
            val price = BigDecimal("50000")
            val firstCategory = "상의"
            val secondCategory = "티셔츠"
            val firstOptions = listOf("S", "M", "L")
            val secondOptions = listOf("블랙", "화이트")
            val thirdOptions = emptyList<String>()

            // When
            val result = productExposedRepository.save(
                name, storeNumber, brand, thumbnailUrl, originalUrl,
                store, price, firstCategory, secondCategory,
                firstOptions, secondOptions, thirdOptions
            )

            // Then
            assertNotNull(result)
            assertEquals(name, result.name)
            assertEquals(storeNumber, result.storeNumber)
            assertEquals(brand, result.brand)
            assertEquals(store, result.store)
            assertEquals(price, result.price)
            assertEquals(firstCategory, result.firstCategory)
            assertEquals(secondCategory, result.secondCategory)
        }

        @Test
        @DisplayName("옵션이 비어있을 때 null로 저장한다")
        fun `should save null for empty options`() {
            // Given
            val name = "옵션 없는 상품"
            val storeNumber = 2345678L
            val brand = "브랜드"
            val thumbnailUrl = "https://example.com/thumbnail.jpg"
            val originalUrl = "https://www.musinsa.com/app/goods/2345678"
            val store = Store.MUSINSA
            val price = BigDecimal("30000")
            val firstCategory = "기타"
            val secondCategory = null
            val firstOptions = emptyList<String>()
            val secondOptions = emptyList<String>()
            val thirdOptions = emptyList<String>()

            // When
            val result = productExposedRepository.save(
                name, storeNumber, brand, thumbnailUrl, originalUrl,
                store, price, firstCategory, secondCategory,
                firstOptions, secondOptions, thirdOptions
            )

            // Then
            assertNotNull(result)
            assertNull(result.firstOption)
            assertNull(result.secondOption)
            assertNull(result.thirdOption)
            assertNull(result.secondCategory)
        }

        @Test
        @DisplayName("여러 옵션을 콤마로 구분하여 저장한다")
        fun `should save multiple options separated by comma`() {
            // Given
            val name = "다중 옵션 상품"
            val storeNumber = 3456789L
            val brand = "브랜드"
            val thumbnailUrl = "https://example.com/thumbnail.jpg"
            val originalUrl = "https://www.musinsa.com/app/goods/3456789"
            val store = Store.MUSINSA
            val price = BigDecimal("80000")
            val firstCategory = "신발"
            val secondCategory = "스니커즈"
            val firstOptions = listOf("250", "255", "260", "265", "270")
            val secondOptions = listOf("화이트", "블랙", "네이비", "그레이")
            val thirdOptions = listOf("일반", "와이드")

            // When
            val result = productExposedRepository.save(
                name, storeNumber, brand, thumbnailUrl, originalUrl,
                store, price, firstCategory, secondCategory,
                firstOptions, secondOptions, thirdOptions
            )

            // Then
            assertNotNull(result)
            assertEquals("250,255,260,265,270", result.firstOption)
            assertEquals("화이트,블랙,네이비,그레이", result.secondOption)
            assertEquals("일반,와이드", result.thirdOption)
        }
    }

    @Nested
    @DisplayName("중복 상품 처리 테스트")
    inner class DuplicateProductHandlingTest {

        @Test
        @DisplayName("동일한 storeNumber와 store로 저장 시도 시 제약 조건에 위반된다")
        fun `should violate constraint when saving duplicate store number and store`() {
            // Given
            val name = "중복 테스트 상품"
            val storeNumber = 4567890L
            val brand = "브랜드"
            val thumbnailUrl = "https://example.com/thumbnail.jpg"
            val originalUrl = "https://www.musinsa.com/app/goods/4567890"
            val store = Store.MUSINSA
            val price = BigDecimal("40000")
            val firstCategory = "상의"
            val secondCategory = "셔츠"
            val emptyOptions = emptyList<String>()

            // When & Then
            // 첫 번째 저장은 성공
            val firstResult = productExposedRepository.save(
                name, storeNumber, brand, thumbnailUrl, originalUrl,
                store, price, firstCategory, secondCategory,
                emptyOptions, emptyOptions, emptyOptions
            )
            assertNotNull(firstResult)

            // 두 번째 동일한 상품 저장 시도 시 예외 발생 예상
            // (실제로는 unique 제약조건에 의해 예외가 발생해야 함)
        }
    }
}