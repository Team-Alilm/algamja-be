package org.team_alilm.application.service

import org.springframework.transaction.annotation.Transactional
import org.team_alilm.application.port.`in`.use_case.RelatedProductsUseCase
import org.team_alilm.application.port.out.LoadProductPort

@Transactional(readOnly = true)
class RelatedProductsService(
    private val loadProductPort: LoadProductPort,
) : RelatedProductsUseCase {

    override fun relatedProducts(command: RelatedProductsUseCase.RelatedProductsCommand): RelatedProductsUseCase.RelatedProductsResult {
        val productList = loadProductPort.loadRelatedProductList(productId = command.productId)
    }
}