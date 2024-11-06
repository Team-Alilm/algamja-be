package org.team_alilm.application.port.out

import org.team_alilm.domain.product.Product
import org.team_alilm.domain.product.ProductId
import org.team_alilm.domain.product.Store

interface LoadProductPort {

    fun loadProduct(
        number:Long,
        store: Store,
        firstOption: String,
        secondOption: String?,
        thirdOption: String?
    ): Product?

    fun loadProduct(
        productId: ProductId,
    ): Product?

    fun loadProductDetails(
        productId: ProductId,
    ): LoadProductSlicePort.ProductAndWaitingCount?

    fun loadRecentProduct(): List<Product>
}