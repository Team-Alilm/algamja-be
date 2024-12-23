package org.team_alilm.adapter.out.persistence.adapter

import org.springframework.stereotype.Component
import org.team_alilm.adapter.out.persistence.mapper.ProductImageMapper
import org.team_alilm.adapter.out.persistence.repository.ProductImageRepository
import org.team_alilm.application.port.out.AddProductImagePort
import org.team_alilm.domain.product.ProductImage

@Component
class ProductImageAdapter(
    private val productImageRepository: ProductImageRepository,
    private val productImageMapper: ProductImageMapper
) : AddProductImagePort {

    override fun add(productImages: List<ProductImage>) {
        val productImageJpaEntitys = productImages.map { productImageMapper.mapToJpaEntity(it) }
        productImageRepository.saveAllIgnore(productImageJpaEntitys)
    }
}