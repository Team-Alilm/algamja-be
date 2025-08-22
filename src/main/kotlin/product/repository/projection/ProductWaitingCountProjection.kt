package org.team_alilm.product.repository.projection

data class ProductWaitingCountProjection(
    val productId: Long,
    val waitingCount: Long
)
