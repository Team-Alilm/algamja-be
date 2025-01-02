package org.team_alilm.adapter.out.persistence.mapper

import domain.product.ProductImage
import org.springframework.stereotype.Component
import org.team_alilm.adapter.out.persistence.entity.ProductImageJpaEntity

@Component
class ProductImageMapper {

    fun mapToJpaEntity(productImage: ProductImage): ProductImageJpaEntity {
        return ProductImageJpaEntity(
            id = productImage.id?.value,
            imageUrl = productImage.imageUrl,
            productNumber = productImage.productNumber,
            productStore = productImage.productStore,
        )
    }

    fun mapToJpaEntityList(productImages: List<ProductImage>): List<ProductImageJpaEntity> {
        return productImages.map {
            ProductImageJpaEntity(
                id = it.id?.value,
                imageUrl = it.imageUrl,
                productNumber = it.productNumber,
                productStore = it.productStore,
            )
        }
    }

    fun mapToDomain(productImageJpaEntity: ProductImageJpaEntity) : ProductImage{
        return ProductImage(
            id = ProductImage.ProductImageId(productImageJpaEntity.id!!),
            imageUrl = productImageJpaEntity.imageUrl,
            productNumber = productImageJpaEntity.productNumber,
            productStore = productImageJpaEntity.productStore,
        )
    }

    fun mapToDomainList(productImageJpaEntityList: List<ProductImageJpaEntity>) = productImageJpaEntityList.map {
        ProductImage(
            id = ProductImage.ProductImageId(it.id!!),
            imageUrl = it.imageUrl,
            productNumber = it.productNumber,
            productStore = it.productStore,
        )
    }
}