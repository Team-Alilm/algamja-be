package org.team_alilm.application.port.out

import org.team_alilm.domain.Product

interface LoadPricePort {

    fun loadPrice(productId: Product.ProductId) : List<PriceHistory>?

    data class PriceHistory(
        val price: Int,
        val date: String
    )
}