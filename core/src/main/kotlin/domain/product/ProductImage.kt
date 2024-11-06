package org.team_alilm.domain.product

class ProductImage(
    val id: ProductImageId?,
    val productId: ProductId,
    val imageUrl: String
) {

    data class ProductImageId(val value: Long)
}