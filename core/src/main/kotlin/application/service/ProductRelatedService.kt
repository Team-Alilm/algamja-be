package org.team_alilm.application.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.team_alilm.application.port.`in`.use_case.ProductRelatedUseCase
import org.team_alilm.application.port.`in`.use_case.ProductRelatedUseCase.*
import org.team_alilm.application.port.out.LoadProductPort
import org.team_alilm.domain.product.ProductId
import org.team_alilm.global.error.NotFoundProductException

@Service
@Transactional(readOnly = true)
class ProductRelatedService(
    private val loadProductPort: LoadProductPort
) : ProductRelatedUseCase {

    override fun productRelated(command: ProductRelatedCommand): ProductRelatedResult {
        val product = loadProductPort.loadProduct(ProductId(command.productId)) ?: throw NotFoundProductException()
        val productRelatedList = loadProductPort.loadRelatedProduct(product.category)

        return ProductRelatedResult(
            productList = productRelatedList
        )
    }
}