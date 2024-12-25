package org.team_alilm.application.port.out

import org.team_alilm.domain.product.ProductImage

interface AddProductImagePort {

    fun add(productImages: List<ProductImage>)
}