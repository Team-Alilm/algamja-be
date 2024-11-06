package org.team_alilm.adapter.out.persistence.mapper

import org.springframework.stereotype.Component
import org.team_alilm.adapter.out.persistence.entity.ProductJpaEntity
import org.team_alilm.domain.product.Product
import org.team_alilm.domain.product.ProductId
import org.team_alilm.domain.product.ProductV2

@Component
class ProductMapper {

    fun mapToJpaEntity(product: Product): ProductJpaEntity {
        return ProductJpaEntity(
            id = product.id?.value,
            number = product.number,
            name = product.name,
            brand = product.brand,
            imageUrl = product.imageUrl,
            store = product.store,
            category = product.category,
            price = product.price,
            firstOption = product.firstOption,
            secondOption = product.secondOption,
            thirdOption = product.thirdOption
        )
    }

    fun mapToJpaEntityV2(product: ProductV2): ProductJpaEntity {
        return ProductJpaEntity(
            id = product.id?.value,
            number = product.number,
            name = product.name,
            brand = product.brand,
            imageUrl = "V2",
            store = product.store,
            category = product.category,
            price = product.price,
            firstOption = product.firstOption,
            secondOption = product.secondOption,
            thirdOption = product.thirdOption
        )
    }

    fun mapToDomainEntityOrNull(productJpaEntity: ProductJpaEntity?): Product? {
        productJpaEntity ?: return null

        return Product(
            id = ProductId(productJpaEntity.id!!),
            number = productJpaEntity.number,
            name = productJpaEntity.name,
            brand = productJpaEntity.brand,
            imageUrl = productJpaEntity.imageUrl,
            store = productJpaEntity.store,
            category = productJpaEntity.category,
            price = productJpaEntity.price,
            firstOption = productJpaEntity.firstOption,
            secondOption = productJpaEntity.secondOption,
            thirdOption = productJpaEntity.thirdOption
        )
    }

    fun mapToDomainEntity(productJpaEntity: ProductJpaEntity): Product {
        return Product(
            id = ProductId(productJpaEntity.id!!),
            number = productJpaEntity.number,
            name = productJpaEntity.name,
            brand = productJpaEntity.brand,
            imageUrl = productJpaEntity.imageUrl,
            store = productJpaEntity.store,
            category = productJpaEntity.category,
            price = productJpaEntity.price,
            firstOption = productJpaEntity.firstOption,
            secondOption = productJpaEntity.secondOption,
            thirdOption = productJpaEntity.thirdOption
        )
    }

    fun mapToDomainEntityV2(productJpaEntity: ProductJpaEntity): ProductV2 {
        return ProductV2(
            id = ProductId(productJpaEntity.id!!),
            number = productJpaEntity.number,
            name = productJpaEntity.name,
            brand = productJpaEntity.brand,
            store = productJpaEntity.store,
            category = productJpaEntity.category,
            price = productJpaEntity.price,
            firstOption = productJpaEntity.firstOption,
            secondOption = productJpaEntity.secondOption,
            thirdOption = productJpaEntity.thirdOption
        )
    }

    fun mapToDomainEntityV2OrNull(productJpaEntity: ProductJpaEntity?): ProductV2? {
        if (productJpaEntity == null) {
            return null
        }

        return ProductV2(
            id = ProductId(productJpaEntity.id!!),
            number = productJpaEntity.number,
            name = productJpaEntity.name,
            brand = productJpaEntity.brand,
            store = productJpaEntity.store,
            category = productJpaEntity.category,
            price = productJpaEntity.price,
            firstOption = productJpaEntity.firstOption,
            secondOption = productJpaEntity.secondOption,
            thirdOption = productJpaEntity.thirdOption
        )
    }

}