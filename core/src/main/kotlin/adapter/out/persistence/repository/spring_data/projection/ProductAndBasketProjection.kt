package org.team_alilm.adapter.out.persistence.repository.spring_data.projection

import org.team_alilm.adapter.out.persistence.entity.BasketJpaEntity
import org.team_alilm.adapter.out.persistence.entity.ProductJpaEntity

data class ProductAndBasketProjection(
    val productJpaEntity: ProductJpaEntity,
    val basketJpaEntity: BasketJpaEntity
) { }