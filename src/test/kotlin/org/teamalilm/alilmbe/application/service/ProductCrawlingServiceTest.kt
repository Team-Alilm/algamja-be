package org.teamalilm.alilmbe.application.service

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertNotNull
import org.mockito.Mock
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import org.teamalilm.alilmbe.application.port.`in`.use_case.ProductCrawlingCommand
import org.teamalilm.alilmbe.application.port.`in`.use_case.ProductCrawlingUseCase
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.web.client.RestClient
import org.teamalilm.alilmbe.application.port.out.ProductDataGateway

@AutoConfigureMockMvc
class ProductCrawlingServiceIntegrationTest {

    @Mock
    val productDataGateway: ProductDataGateway

    @Mock
    val restClient: RestClient

    @Test
    fun `should return product crawling result`() {
        // Given
        val command = ProductCrawlingCommand("https://www.musinsa.com/app/goods/4175204")

        // When
        val result = ProductCrawlingService(
            productDataGateway = productDataGateway,
            restClient = restClient
        ).invoke(command)

        // Then
        assertNotNull(result)
        println(result)
        // 추가 검증을 통해 결과가 예상대로인지 확인합니다.
        assert(result.name.isNotEmpty())
        assert(result.price > 0)
    }
}
