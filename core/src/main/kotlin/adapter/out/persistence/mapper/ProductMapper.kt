package org.team_alilm.adapter.out.persistence.mapper

import domain.product.Product
import domain.product.ProductId
import org.springframework.stereotype.Component
import org.team_alilm.adapter.out.persistence.entity.ProductJpaEntity

@Component
class ProductMapper {

    fun mapToJpaEntity(product: Product): ProductJpaEntity {
        return ProductJpaEntity(
            id = product.id?.value,
            number = product.number,
            name = product.name,
            brand = product.brand,
            thumbnailUrl = product.thumbnailUrl,
            store = product.store,
            firstCategory = product.firstCategory,
            secondCategory = product.secondCategory,
            price = product.price,
            firstOption = product.firstOption,
            secondOption = product.secondOption,
            thirdOption = product.thirdOption
        )
    }

    fun mapToDomainEntityOrNull(productJpaEntity: ProductJpaEntity?): Product? {
        productJpaEntity ?: return null

        return product(productJpaEntity)
    }

    fun mapToDomainEntity(productJpaEntity: ProductJpaEntity): Product {
        return product(productJpaEntity)
    }

    private fun product(productJpaEntity: ProductJpaEntity) = Product(
        id = ProductId(productJpaEntity.id!!),
        number = productJpaEntity.number,
        name = productJpaEntity.name,
        brand = productJpaEntity.brand,
        thumbnailUrl = productJpaEntity.thumbnailUrl,
        store = productJpaEntity.store,
        firstCategory = productJpaEntity.firstCategory,
        secondCategory = productJpaEntity.secondCategory,
        price = productJpaEntity.price,
        firstOption = productJpaEntity.firstOption,
        secondOption = productJpaEntity.secondOption,
        thirdOption = productJpaEntity.thirdOption
    )
}