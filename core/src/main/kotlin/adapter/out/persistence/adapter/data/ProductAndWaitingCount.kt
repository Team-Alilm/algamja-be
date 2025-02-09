package org.team_alilm.adapter.out.persistence.adapter.data

import domain.product.Product

data class ProductAndWaitingCount(
    val product: Product,
    val waitingCount: Long
)