package org.team_alilm.application.port.use_case

import domain.product.Store
import org.springframework.stereotype.Component
import org.team_alilm.application.service.AblyProductCrawlingService
import org.team_alilm.application.service.CM29ProductCrawlingService
import org.team_alilm.application.service.MusinsaProductCrawlingService

@Component
class ProductCrawlingUseCaseResolver(
    private val muSinSaProductCrawlingUseCase: MusinsaProductCrawlingService,
    private val aBlyProductCrawlingUseCase: AblyProductCrawlingService,
    private val cm29ProductCrawlingUseCase: CM29ProductCrawlingService,
) {

    fun resolve(store: Store): ProductCrawlingUseCase {
        return when (store) {
            Store.CM29 -> cm29ProductCrawlingUseCase
            Store.MUSINSA -> muSinSaProductCrawlingUseCase
            Store.A_BLY -> aBlyProductCrawlingUseCase
            Store.NONE -> throw IllegalArgumentException("Unknown store")
        }
    }
}