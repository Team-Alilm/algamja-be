package org.team_alilm.algamja.product.repository.projection

import org.team_alilm.algamja.product.entity.ProductRow

data class ProductSliceProjection(
    val productRows : List<ProductRow>,
    val hasNext: Boolean,
)