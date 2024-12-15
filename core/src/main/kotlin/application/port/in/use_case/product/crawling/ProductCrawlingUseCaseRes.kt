package org.team_alilm.application.port.`in`.use_case.product.crawling

import org.springframework.stereotype.Component
import org.team_alilm.application.service.AblyProductCrawlingService
import org.team_alilm.application.service.MusinsaProductCrawlingService
import org.team_alilm.domain.product.Store

@Component
class ProductCrawlingUseCaseResolver(
    private val muSinSaProductCrawlingUseCase: MusinsaProductCrawlingService,
    private val aBlyProductCrawlingUseCase: AblyProductCrawlingService,
) {

    fun resolve(store: Store): ProductCrawlingUseCase {
        return when (store) {
            Store.MUSINSA -> muSinSaProductCrawlingUseCase
            Store.A_BLY -> aBlyProductCrawlingUseCase
        }
    }
}