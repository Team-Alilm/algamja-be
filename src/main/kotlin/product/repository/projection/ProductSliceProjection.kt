package org.team_alilm.product.repository.projection

import org.team_alilm.product.entity.ProductRow

data class ProductSliceProjection(
    val productRows : List<ProductRow>,
    val hasNext: Boolean,
)