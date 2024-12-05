package org.team_alilm.application.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.team_alilm.application.port.`in`.use_case.ProductCrawlingUseCase
import org.team_alilm.domain.product.Store

@Service
@Transactional(readOnly = true)
class ProductCrawlingService : ProductCrawlingUseCase {

    override fun crawling(command: ProductCrawlingUseCase.ProductCrawlingCommand): ProductCrawlingUseCase.CrawlingResult {
        TODO("Not yet implemented")
    }

    private fun getStore(url: String): Store {

        return
    }
}