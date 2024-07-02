package org.teamalilm.alilmbe.service

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

class ProductCrawlingServiceTest {

    val productCrawlingService = ProductCrawlingService()

    @Test
    fun crawling() {
        val command = ProductCrawlingService.ProductCrawlingCommand(
            url = "https://www.musinsa.com/app/goods/3262292"
        )

        val result = productCrawlingService.crawling(command)

        println(result)
    }
}