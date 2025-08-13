package org.team_alilm.product.repository.projection

data class ProductProjection(
    val id: Long,
    val storeNumber: Long,
    val name: String,
    val brand: String,
    val thumbnailUrl: String,
    val store: String,
    val price: Int,
    val firstCategory: String,
    val secondCategory: String?,
    val firstOption: String?,
    val secondOption: String?,
    val thirdOption: String?
)
