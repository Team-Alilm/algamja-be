package org.team_alilm.adapter.out.persistence.repository.basket

import org.team_alilm.adapter.out.persistence.entity.BasketJpaEntity
import org.team_alilm.adapter.out.persistence.entity.ProductJpaEntity

data class BasketAndProductProjection(
    val basketJpaEntity: BasketJpaEntity,
    val productJpaEntity: ProductJpaEntity,
    val waitingCount: Long
)