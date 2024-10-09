package org.team_alilm.adapter.out.persistence.mapper

import org.springframework.stereotype.Component
import org.teamalilm.alilm.adapter.out.persistence.entity.PriceJpaEntity
import org.teamalilm.alilm.adapter.out.persistence.entity.ProductJpaEntity
import org.teamalilm.alilm.domain.Price
import org.teamalilm.alilm.domain.Product

@Component
class PriceMapper {

    fun mapToJpaEntity(price: Int, productJpaEntity: ProductJpaEntity): PriceJpaEntity {
        return PriceJpaEntity(
            null,
            price = price,
            productJpaEntity = productJpaEntity
        )
    }

    fun mapToDomainEntity(priceJpaEntity: PriceJpaEntity): Price {
        return Price(
            id = Price.PriceId(priceJpaEntity.id),
            price = priceJpaEntity.price,
            productId = Product.ProductId(priceJpaEntity.productJpaEntity.id!!)
        )
    }

    fun mapToDomainEntityOrNull(priceJpaEntity: PriceJpaEntity?): Price? {
        return priceJpaEntity?.let { mapToDomainEntity(it) }
    }

}
