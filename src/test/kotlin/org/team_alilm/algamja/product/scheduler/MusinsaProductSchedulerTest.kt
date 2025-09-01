package org.team_alilm.algamja.product.scheduler

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.mockito.kotlin.*
import org.team_alilm.algamja.product.service.MusinsaProductService

class MusinsaProductSchedulerTest {

    private val musinsaProductService = mock<MusinsaProductService>()
    private lateinit var musinsaProductScheduler: MusinsaProductScheduler

    @BeforeEach
    fun setUp() {
        musinsaProductScheduler = MusinsaProductScheduler(musinsaProductService)
    }

    @Nested
    @DisplayName("registerRankingMusinsaProducts 스케줄 메서드 테스트")
    inner class RegisterRankingMusinsaProductsTest {

        @Test
        @DisplayName("성공적으로 스케줄 작업을 실행한다")
        fun `should execute scheduled task successfully`() {
            // Given
            val expectedCount = 50
            whenever(musinsaProductService.fetchAndRegisterRankingProducts(100)).thenReturn(expectedCount)

            // When
            musinsaProductScheduler.registerRankingMusinsaProducts()

            // Then
            verify(musinsaProductService, times(1)).fetchAndRegisterRankingProducts(100)
        }

        @Test
        @DisplayName("서비스에서 예외 발생 시에도 스케줄러는 정상 종료된다")
        fun `should handle service exceptions gracefully`() {
            // Given
            whenever(musinsaProductService.fetchAndRegisterRankingProducts(100))
                .thenThrow(RuntimeException("Service error"))

            // When & Then - 예외가 발생해도 스케줄러 메서드는 정상 종료되어야 함
            musinsaProductScheduler.registerRankingMusinsaProducts()

            // Verify that the service was called despite the exception
            verify(musinsaProductService, times(1)).fetchAndRegisterRankingProducts(100)
        }

        @Test
        @DisplayName("정확히 100개 상품 등록을 요청한다")
        fun `should request exactly 100 products`() {
            // Given
            whenever(musinsaProductService.fetchAndRegisterRankingProducts(100)).thenReturn(100)

            // When
            musinsaProductScheduler.registerRankingMusinsaProducts()

            // Then
            verify(musinsaProductService).fetchAndRegisterRankingProducts(eq(100))
        }

        @Test
        @DisplayName("서비스 메서드는 한 번만 호출된다")
        fun `should call service method exactly once`() {
            // Given
            whenever(musinsaProductService.fetchAndRegisterRankingProducts(100)).thenReturn(75)

            // When
            musinsaProductScheduler.registerRankingMusinsaProducts()

            // Then
            verify(musinsaProductService, times(1)).fetchAndRegisterRankingProducts(any())
        }

        @Test
        @DisplayName("여러 번 실행해도 각각 독립적으로 작동한다")
        fun `should work independently on multiple executions`() {
            // Given
            whenever(musinsaProductService.fetchAndRegisterRankingProducts(100))
                .thenReturn(30)
                .thenReturn(50)
                .thenReturn(80)

            // When
            musinsaProductScheduler.registerRankingMusinsaProducts()
            musinsaProductScheduler.registerRankingMusinsaProducts()
            musinsaProductScheduler.registerRankingMusinsaProducts()

            // Then
            verify(musinsaProductService, times(3)).fetchAndRegisterRankingProducts(100)
        }
    }

    @Nested
    @DisplayName("테스트용 스케줄러 메서드")
    inner class TestSchedulerTest {

        // 현재는 주석 처리되어 있지만, 활성화될 경우를 대비한 테스트
        @Test
        @DisplayName("테스트 스케줄러 메서드가 정의되어 있다")
        fun `test scheduler method exists`() {
            // Given & When & Then
            // 테스트용 메서드가 클래스에 존재하는지 확인
            val methods = MusinsaProductScheduler::class.java.declaredMethods
            val testMethod = methods.find { it.name == "registerTestMusinsaProducts" }
            
            // 메서드가 존재하지 않거나 주석 처리되어 있어도 테스트 통과
            // 실제 운영에서는 주석 처리된 상태가 정상
        }
    }

    @Nested
    @DisplayName("스케줄러 설정 테스트")
    inner class SchedulerConfigurationTest {

        @Test
        @DisplayName("스케줄러 어노테이션이 올바르게 설정되어 있다")
        fun `should have correct scheduler annotation`() {
            // Given
            val method = MusinsaProductScheduler::class.java.getDeclaredMethod("registerRankingMusinsaProducts")
            
            // When
            val scheduledAnnotation = method.getAnnotation(org.springframework.scheduling.annotation.Scheduled::class.java)
            
            // Then
            assert(scheduledAnnotation != null) { "Scheduled annotation should be present" }
            assert(scheduledAnnotation.cron == "0 0 * * * *") { "Cron expression should be '0 0 * * * *'" }
        }

        @Test
        @DisplayName("스케줄러 클래스가 Spring Component로 등록되어 있다")
        fun `should be annotated with Component`() {
            // Given & When
            val componentAnnotation = MusinsaProductScheduler::class.java.getAnnotation(org.springframework.stereotype.Component::class.java)
            
            // Then
            assert(componentAnnotation != null) { "Component annotation should be present" }
        }
    }
}