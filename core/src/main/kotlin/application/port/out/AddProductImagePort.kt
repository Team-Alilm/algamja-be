package org.team_alilm.application.port.out

import domain.product.ProductImage

interface AddProductImagePort {

    fun add(productImages: List<ProductImage>)
}