package org.team_alilm.application.port.`in`.use_case

import org.team_alilm.domain.product.Product

interface ProductRelatedUseCase {

    fun productRelated(
        command: ProductRelatedCommand
    ): ProductRelatedResult

    data class ProductRelatedCommand(
        val productId: Long
    )

    data class ProductRelatedResult(
        val productList: List<Product>
    )
}