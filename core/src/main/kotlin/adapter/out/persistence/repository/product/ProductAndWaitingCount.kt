package org.team_alilm.adapter.out.persistence.repository.product

import domain.product.Product

data class ProductAndWaitingCount(
    val product: Product,
    val waitingCount: Long
)
