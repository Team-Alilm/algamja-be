package org.team_alilm.application.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.team_alilm.application.port.`in`.use_case.ProductCrawlingUseCase
import org.team_alilm.application.port.out.gateway.crawling.CrawlingGateway
import org.team_alilm.application.port.out.gateway.crawling.CrawlingGatewayResolver
import org.team_alilm.domain.product.Store
import org.team_alilm.global.error.NotFoundStoreException

@Service
@Transactional(readOnly = true)
class ProductCrawlingService(
    private val crawlingGatewayResolver: CrawlingGatewayResolver
) : ProductCrawlingUseCase {

    override fun crawling(command: ProductCrawlingUseCase.ProductCrawlingCommand): ProductCrawlingUseCase.CrawlingResult {
        val store = getStore(command.url)
        val crawlingGateway = crawlingGatewayResolver.resolve(store)

        return ProductCrawlingUseCase.CrawlingResult(
            number = 0,
            name = "test",
            brand = "test",
            thumbnailUrl = "test",
            firstCategory = "test",
            secnedCategory = "test",
            price = 0,
            store = Store.A_BLY,
            firstOption = "test",
            secondOption = null,
            thirdOption = null
        )
    }

    private fun getStore(url: String): Store {
        return when {
            url.contains("musinsa") -> Store.MUSINSA
            url.contains("a-bly") -> Store.A_BLY
            else -> throw NotFoundStoreException()
        }
    }
}