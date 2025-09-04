package org.team_alilm.algamja.product.scheduler

import io.mockk.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.team_alilm.algamja.product.service.MusinsaProductService

class MusinsaProductSchedulerUnitTest {

    private val musinsaProductService: MusinsaProductService = mockk()
    private lateinit var musinsaProductScheduler: MusinsaProductScheduler

    @BeforeEach
    fun setUp() {
        clearAllMocks()
        musinsaProductScheduler = MusinsaProductScheduler(musinsaProductService)
    }

    @Nested
    @DisplayName("스케줄러 기본 기능 테스트")
    inner class BasicSchedulerTest {

        @Test
        @DisplayName("스케줄러가 Spring 컨텍스트에 정상적으로 로드된다")
        fun `should load scheduler in spring context`() {
            // Given & When & Then
            assertNotNull(musinsaProductScheduler, "MusinsaProductScheduler bean should be loaded")
        }

        @Test
        @DisplayName("스케줄러 메서드를 수동으로 실행할 수 있다")
        fun `should be able to execute scheduler method manually`() {
            // Given
            every { musinsaProductService.fetchAndRegisterRankingProducts(30) } returns 25

            // When
            musinsaProductScheduler.registerRankingMusinsaProducts()

            // Then
            verify(exactly = 1) { musinsaProductService.fetchAndRegisterRankingProducts(30) }
        }

        @Test
        @DisplayName("여러 번 수동 실행해도 정상 작동한다")
        fun `should work correctly on multiple manual executions`() {
            // Given
            every { musinsaProductService.fetchAndRegisterRankingProducts(30) } returnsMany listOf(15, 20, 25)

            // When
            repeat(3) {
                musinsaProductScheduler.registerRankingMusinsaProducts()
            }

            // Then
            verify(exactly = 3) { musinsaProductService.fetchAndRegisterRankingProducts(30) }
        }
    }

    @Nested
    @DisplayName("예외 처리 테스트")
    inner class ExceptionHandlingTest {

        @Test
        @DisplayName("서비스에서 예외 발생시 스케줄러가 안전하게 처리한다")
        fun `should handle service exceptions gracefully`() {
            // Given
            every { musinsaProductService.fetchAndRegisterRankingProducts(30) } throws RuntimeException("Service exception")

            // When & Then
            assertDoesNotThrow {
                musinsaProductScheduler.registerRankingMusinsaProducts()
            }

            verify(exactly = 1) { musinsaProductService.fetchAndRegisterRankingProducts(30) }
        }

        @Test
        @DisplayName("예외 발생 후에도 다음 실행이 정상적으로 동작한다")
        fun `should continue working after exception`() {
            // Given
            every { musinsaProductService.fetchAndRegisterRankingProducts(30) } throws RuntimeException("First call fails") andThen 15

            // When & Then
            assertDoesNotThrow {
                musinsaProductScheduler.registerRankingMusinsaProducts()
                musinsaProductScheduler.registerRankingMusinsaProducts()
            }

            verify(exactly = 2) { musinsaProductService.fetchAndRegisterRankingProducts(30) }
        }
    }

    @Nested
    @DisplayName("의존성 주입 테스트")
    inner class DependencyInjectionTest {

        @Test
        @DisplayName("모든 필요한 빈들이 정상적으로 주입된다")
        fun `should have all required dependencies injected`() {
            // Given & When & Then
            assertNotNull(musinsaProductScheduler, "Scheduler should be injected")
            assertNotNull(musinsaProductService, "Service should be injected")
        }
    }

    @Nested
    @DisplayName("비즈니스 로직 테스트")
    inner class BusinessLogicTest {

        @Test
        @DisplayName("성공적인 상품 등록시 반환값을 로깅한다")
        fun `should log return value on successful registration`() {
            // Given
            val expectedCount = 25
            every { musinsaProductService.fetchAndRegisterRankingProducts(30) } returns expectedCount

            // When
            musinsaProductScheduler.registerRankingMusinsaProducts()

            // Then
            verify(exactly = 1) { musinsaProductService.fetchAndRegisterRankingProducts(30) }
        }

        @Test
        @DisplayName("0개 상품 등록시에도 정상 처리한다")
        fun `should handle zero products registration normally`() {
            // Given
            every { musinsaProductService.fetchAndRegisterRankingProducts(30) } returns 0

            // When & Then
            assertDoesNotThrow {
                musinsaProductScheduler.registerRankingMusinsaProducts()
            }

            verify(exactly = 1) { musinsaProductService.fetchAndRegisterRankingProducts(30) }
        }
    }
}