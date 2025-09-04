package org.team_alilm.algamja.product.repository

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Assertions.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import org.team_alilm.algamja.common.enums.Store
import java.math.BigDecimal

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ProductExposedRepositoryTest {

    @Autowired
    private lateinit var productExposedRepository: ProductExposedRepository

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
        @DisplayName("상품 조회가 정상 작동한다")
        fun `should fetch product successfully`() {
            // Given
            val name = "테스트 조회 상품"
            val storeNumber = 1234567L
            val store = Store.MUSINSA
            
            // 먼저 상품을 저장
            val savedProduct = productExposedRepository.save(
                name = name,
                storeNumber = storeNumber,
                brand = "테스트 브랜드",
                thumbnailUrl = "https://example.com/thumbnail.jpg",
                store = store,
                price = BigDecimal("50000"),
                firstCategory = "상의",
                secondCategory = "티셔츠",
                firstOption = "M",
                secondOption = "블랙",
                thirdOption = null
            )

            // When
            val result = productExposedRepository.fetchProductByStoreNumber(storeNumber, store)

            // Then
            assertNotNull(result)
            assertEquals(savedProduct.id, result?.id)
            assertEquals(name, result?.name)
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
            val storeNumber = 1234568L
            val brand = "테스트 브랜드"
            val thumbnailUrl = "https://example.com/thumbnail.jpg"
            // val originalUrl = "https://www.musinsa.com/app/goods/1234568"
            val store = Store.MUSINSA
            val price = BigDecimal("50000")
            val firstCategory = "상의"
            val secondCategory = "티셔츠"
            val firstOption = "S"
            val secondOption = "블랙"
            val thirdOption = null

            // When
            val result = productExposedRepository.save(
                name = name,
                storeNumber = storeNumber,
                brand = brand,
                thumbnailUrl = thumbnailUrl,
                store = store,
                price = price,
                firstCategory = firstCategory,
                secondCategory = secondCategory,
                firstOption = firstOption,
                secondOption = secondOption,
                thirdOption = thirdOption
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
            assertEquals(firstOption, result.firstOption)
            assertEquals(secondOption, result.secondOption)
            assertNull(result.thirdOption)
        }

        @Test
        @DisplayName("옵션이 null일 때 null로 저장한다")
        fun `should save null for null options`() {
            // Given
            val name = "옵션 없는 상품"
            val storeNumber = 2345678L
            val brand = "브랜드"
            val thumbnailUrl = "https://example.com/thumbnail.jpg"
            // val originalUrl = "https://www.musinsa.com/app/goods/2345678"
            val store = Store.MUSINSA
            val price = BigDecimal("30000")
            val firstCategory = "기타"
            val secondCategory = null
            val firstOption = "기본"
            val secondOption = null
            val thirdOption = null

            // When
            val result = productExposedRepository.save(
                name = name,
                storeNumber = storeNumber,
                brand = brand,
                thumbnailUrl = thumbnailUrl,
                store = store,
                price = price,
                firstCategory = firstCategory,
                secondCategory = secondCategory,
                firstOption = firstOption,
                secondOption = secondOption,
                thirdOption = thirdOption
            )

            // Then
            assertNotNull(result)
            assertEquals("기본", result.firstOption)
            assertNull(result.secondOption)
            assertNull(result.thirdOption)
            assertNull(result.secondCategory)
        }

        @Test
        @DisplayName("옵션 문자열로 저장한다")
        fun `should save options as string`() {
            // Given
            val name = "옵션 상품"
            val storeNumber = 3456789L
            val brand = "브랜드"
            val thumbnailUrl = "https://example.com/thumbnail.jpg"
            // val originalUrl = "https://www.musinsa.com/app/goods/3456789"
            val store = Store.MUSINSA
            val price = BigDecimal("80000")
            val firstCategory = "신발"
            val secondCategory = "스니커즈"
            val firstOption = "250"
            val secondOption = "화이트"
            val thirdOption = "일반"

            // When
            val result = productExposedRepository.save(
                name = name,
                storeNumber = storeNumber,
                brand = brand,
                thumbnailUrl = thumbnailUrl,
                store = store,
                price = price,
                firstCategory = firstCategory,
                secondCategory = secondCategory,
                firstOption = firstOption,
                secondOption = secondOption,
                thirdOption = thirdOption
            )

            // Then
            assertNotNull(result)
            assertEquals("250", result.firstOption)
            assertEquals("화이트", result.secondOption)
            assertEquals("일반", result.thirdOption)
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
            // val originalUrl = "https://www.musinsa.com/app/goods/4567890"
            val store = Store.MUSINSA
            val price = BigDecimal("40000")
            val firstCategory = "상의"
            val secondCategory = "셔츠"
            val firstOption = "일반"
            val secondOption = null
            val thirdOption = null

            // When & Then
            // 첫 번째 저장은 성공
            val firstResult = productExposedRepository.save(
                name = name,
                storeNumber = storeNumber,
                brand = brand,
                thumbnailUrl = thumbnailUrl,
                store = store,
                price = price,
                firstCategory = firstCategory,
                secondCategory = secondCategory,
                firstOption = firstOption,
                secondOption = secondOption,
                thirdOption = thirdOption
            )
            assertNotNull(firstResult)

            // 두 번째 동일한 상품 저장 시도 시 예외 발생
            assertThrows(Exception::class.java) {
                productExposedRepository.save(
                    name = name,
                    storeNumber = storeNumber,
                    brand = brand,
                    thumbnailUrl = thumbnailUrl,
                        store = store,
                    price = price,
                    firstCategory = firstCategory,
                    secondCategory = secondCategory,
                    firstOption = firstOption,
                    secondOption = secondOption,
                    thirdOption = thirdOption
                )
            }
        }
    }
}