package org.team_alilm.product.image.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.team_alilm.product.image.entity.ProductImage

interface ProductImageRepository : JpaRepository<ProductImage, Long> {

    fun findAllByProductId(productId: Long) : List<ProductImage>
}
