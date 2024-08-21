package org.teamalilm.alilm.application.port.out

import org.teamalilm.alilm.domain.Product

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
}