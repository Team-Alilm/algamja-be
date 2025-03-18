package org.team_alilm.adapter.out.persistence.adapter.data

import domain.Basket
import domain.product.Product

data class ProductAndBasket(
    val product: Product,
    val basket: Basket
)