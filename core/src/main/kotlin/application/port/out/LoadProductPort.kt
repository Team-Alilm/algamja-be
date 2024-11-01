package org.team_alilm.application.port.out

import org.team_alilm.domain.Product

interface LoadProductPort {

    fun loadProduct(
        number:Long,
        store: Product.Store,
        firstOption: String,
        secondOption: String?,
        thirdOption: String?
    ): Product?

    fun loadProduct(
        productId: Product.ProductId,
    ): Product?

    fun loadProductDetails(
        productId: Product.ProductId,
    ): LoadProductSlicePort.ProductAndWaitingCount?

    fun loadRecentProduct(): List<Product>
}