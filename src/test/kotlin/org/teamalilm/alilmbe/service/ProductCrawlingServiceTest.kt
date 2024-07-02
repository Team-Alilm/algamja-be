package org.teamalilm.alilmbe.service

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

import org.teamalilm.alilmbe.service.crawling.ProductCrawlingService

class ProductCrawlingServiceTest {

    val productCrawlingService = ProductCrawlingService()

    @Test
    fun crawling() {
        // given
        val command = ProductCrawlingService.ProductCrawlingCommand(
            url = "https://www.musinsa.com/app/goods/3262292"
        )

        // when
        val result = productCrawlingService.crawling(command)

        // then
        Assertions.assertThat(result.name).isEqualTo("오버사이즈 립 포켓 하프 셔츠 블랙")
    }
}