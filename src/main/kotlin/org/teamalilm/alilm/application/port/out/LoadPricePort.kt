package org.teamalilm.alilm.application.port.out

import org.teamalilm.alilm.domain.Product

interface LoadPricePort {

    fun loadPrice(productId: Product.ProductId) : List<PriceHistory>?

    data class PriceHistory(
        val price: Int,
        val date: String
    )
}