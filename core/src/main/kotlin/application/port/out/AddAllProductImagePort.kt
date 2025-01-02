package org.team_alilm.application.port.out

import domain.product.ProductImage

interface AddAllProductImagePort {

    fun addAllProductImage(productImageList: List<ProductImage>)
}