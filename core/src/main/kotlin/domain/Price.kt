package org.team_alilm.domain

import org.team_alilm.domain.Product.ProductId

class Price (
    val id: PriceId? = null,
    val price: Int,
    val productId: ProductId
) {

    data class PriceId(val value: Long?)
}