package org.team_alilm.algamja.product.repository.projection

data class ProductWaitingCountProjection(
    val productId: Long,
    val waitingCount: Long
)
