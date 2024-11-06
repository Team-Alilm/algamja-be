package org.team_alilm.adapter.out.persistence.mapper

import org.springframework.stereotype.Component
import org.team_alilm.adapter.out.persistence.entity.ProductImageJpaEntity
import org.team_alilm.domain.product.ProductId
import org.team_alilm.domain.product.ProductImage

@Component
class ProductImageMapper {

    fun mapToJpaEntity(productImage: ProductImage): ProductImageJpaEntity {
        return ProductImageJpaEntity(
            id = productImage.id?.value,
            productId = productImage.productId.value,
            imageUrl = productImage.imageUrl,
        )
    }

    fun mapToDomain(productImageJpaEntity: ProductImageJpaEntity) : ProductImage{
        return ProductImage(
            id = ProductImage.ProductImageId(productImageJpaEntity.id!!),
            productId = ProductId(productImageJpaEntity.productId),
            imageUrl = productImageJpaEntity.imageUrl,
        )
    }
}