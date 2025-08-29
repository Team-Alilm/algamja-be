package org.team_alilm.algamja.product.scheduler

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.mock.mockito.SpyBean
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestPropertySource
import org.mockito.kotlin.*
import org.springframework.web.client.RestClient
import org.team_alilm.algamja.product.crawler.CrawlerRegistry
import org.team_alilm.algamja.product.image.repository.ProductImageExposedRepository
import org.team_alilm.algamja.product.repository.ProductExposedRepository
import org.team_alilm.algamja.product.service.MusinsaProductService
import java.util.concurrent.TimeUnit
import kotlin.test.assertTrue

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = [
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.jpa.hibernate.ddl-auto=create-drop"
])
@EnableScheduling
class MusinsaProductSchedulerIntegrationTest {

    @SpyBean
    private lateinit var musinsaProductScheduler: MusinsaProductScheduler

    @MockBean
    private lateinit var musinsaProductService: MusinsaProductService

    @MockBean
    private lateinit var restClient: RestClient

    @MockBean
    private lateinit var crawlerRegistry: CrawlerRegistry

    @MockBean
    private lateinit var productExposedRepository: ProductExposedRepository

    @MockBean
    private lateinit var productImageExposedRepository: ProductImageExposedRepository

    @Nested
    @DisplayName("스케줄링 통합 테스트")
    inner class SchedulingIntegrationTest {

        @Test
        @DisplayName("스케줄러가 Spring 컨텍스트에 정상적으로 로드된다")
        fun `should load scheduler in spring context`() {
            // Given & When & Then
            // 스프링 컨텍스트가 정상적으로 로드되고 스케줄러 빈이 존재하는지 확인
            assert(musinsaProductScheduler != null) { "MusinsaProductScheduler bean should be loaded" }
        }

        @Test
        @DisplayName("스케줄러 메서드를 수동으로 실행할 수 있다")
        fun `should be able to execute scheduler method manually`() {
            // Given
            whenever(musinsaProductService.fetchAndRegisterRandomProducts(100)).thenReturn(50)

            // When
            musinsaProductScheduler.registerRandomMusinsaProducts()

            // Then
            verify(musinsaProductService, times(1)).fetchAndRegisterRandomProducts(100)
        }

        @Test
        @DisplayName("여러 번 수동 실행해도 정상 작동한다")
        fun `should work correctly on multiple manual executions`() {
            // Given
            whenever(musinsaProductService.fetchAndRegisterRandomProducts(100))
                .thenReturn(30, 45, 60)

            // When
            repeat(3) {
                musinsaProductScheduler.registerRandomMusinsaProducts()
            }

            // Then
            verify(musinsaProductService, times(3)).fetchAndRegisterRandomProducts(100)
        }

        @Test
        @DisplayName("예외 상황에서도 안정적으로 처리된다")
        fun `should handle exceptions gracefully in integration environment`() {
            // Given
            whenever(musinsaProductService.fetchAndRegisterRandomProducts(100))
                .thenThrow(RuntimeException("Integration test exception"))

            // When & Then
            // 예외가 발생해도 애플리케이션이 중단되지 않아야 함
            try {
                musinsaProductScheduler.registerRandomMusinsaProducts()
            } catch (e: Exception) {
                // 스케줄러 내부에서 예외를 처리하므로 여기까지 예외가 전파되지 않아야 함
                assert(false) { "Exception should be handled within scheduler" }
            }

            verify(musinsaProductService, times(1)).fetchAndRegisterRandomProducts(100)
        }
    }

    @Nested
    @DisplayName("의존성 주입 테스트")
    inner class DependencyInjectionTest {

        @Test
        @DisplayName("모든 필요한 빈들이 정상적으로 주입된다")
        fun `should have all required dependencies injected`() {
            // Given & When & Then
            assert(musinsaProductScheduler != null) { "Scheduler should be injected" }
            assert(musinsaProductService != null) { "Service should be injected" }
            assert(restClient != null) { "RestClient should be injected" }
            assert(crawlerRegistry != null) { "CrawlerRegistry should be injected" }
            assert(productExposedRepository != null) { "ProductRepository should be injected" }
            assert(productImageExposedRepository != null) { "ProductImageRepository should be injected" }
        }
    }

    @Nested
    @DisplayName("프로파일별 테스트")
    inner class ProfileBasedTest {

        @Test
        @DisplayName("테스트 프로파일에서 정상 작동한다")
        fun `should work correctly in test profile`() {
            // Given
            whenever(musinsaProductService.fetchAndRegisterRandomProducts(100)).thenReturn(25)

            // When
            musinsaProductScheduler.registerRandomMusinsaProducts()

            // Then
            verify(musinsaProductService, times(1)).fetchAndRegisterRandomProducts(100)
        }
    }
}