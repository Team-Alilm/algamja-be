package org.team_alilm.algamja.product.image.repository.projection

data class ProductImageProjection(
    val id: Long,
    val productId: Long,
    val imageUrl: String
)
