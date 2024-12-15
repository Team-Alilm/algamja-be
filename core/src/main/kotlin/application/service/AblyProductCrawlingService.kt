package org.team_alilm.application.service

import org.springframework.stereotype.Service
import org.team_alilm.application.port.`in`.use_case.product.crawling.ProductCrawlingUseCase
import org.team_alilm.domain.product.Store

@Service
class AblyProductCrawlingService : ProductCrawlingUseCase {
    override fun crawling(command: ProductCrawlingUseCase.ProductCrawlingCommand): ProductCrawlingUseCase.CrawlingResult {
        return ProductCrawlingUseCase.CrawlingResult(
            number = 0L,
            name = "Example Name",
            brand = "Example Brand",
            thumbnailUrl = "https://example.com",
            firstCategory = "Example Category",
            secondCategory = "Example Subcategory",
            price = 0,
            store = Store.A_BLY,
        )
    }
}