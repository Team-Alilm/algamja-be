package org.team_alilm.application.port.out

import org.teamalilm.alilm.domain.Product

interface LoadPricePort {

    fun loadPrice(productId: Product.ProductId) : List<org.team_alilm.application.port.out.LoadPricePort.PriceHistory>?

    data class PriceHistory(
        val price: Int,
        val date: String
    )
}