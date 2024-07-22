package org.teamalilm.alilmbe.application.service

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertNotNull
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import org.teamalilm.alilmbe.application.port.`in`.use_case.ProductCrawlingCommand
import org.teamalilm.alilmbe.application.port.`in`.use_case.ProductCrawlingUseCase
import org.springframework.beans.factory.annotation.Autowired

@SpringBootTest
@Transactional  // 각 테스트가 완료된 후 트랜잭션을 롤백하여 데이터베이스 상태를 정리합니다.
class ProductCrawlingServiceIntegrationTest {

    @Autowired
    private lateinit var useCase: ProductCrawlingUseCase

    @Test
    fun `should return product crawling result`() {
        // Given
        val command = ProductCrawlingCommand("https://store.musinsa.com/app/goods/305485")

        // When
        val result = useCase.invoke(command)

        // Then
        assertNotNull(result)
        println(result)
        // 추가 검증을 통해 결과가 예상대로인지 확인합니다.
        assert(result.name.isNotEmpty())
        assert(result.price > 0)
    }
}
