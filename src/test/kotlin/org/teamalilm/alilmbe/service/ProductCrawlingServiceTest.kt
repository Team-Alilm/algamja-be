package org.teamalilm.alilmbe.service

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

import org.teamalilm.alilmbe.service.crawling.ProductCrawlingService

class ProductCrawlingServiceTest {

    private val productCrawlingService = ProductCrawlingService()

    @Test
    fun crawling() {
        // given
        val command = ProductCrawlingService.ProductCrawlingCommand(
            url = "https%3A%2F%2Fwww.musinsa.com%2Fapp%2Fgoods%2F3262292"
        )

        // when
        val result = productCrawlingService.crawling(command)

        // then
        Assertions.assertThat(result.name).isEqualTo("오버사이즈 립 포켓 하프 셔츠 블랙")
    }
}