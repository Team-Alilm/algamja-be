package org.team_alilm.application.port.out

import org.team_alilm.domain.product.ProductImage

interface AddAllProductImagePort {

    fun addAllProductImage(productImageList: List<ProductImage>)
}