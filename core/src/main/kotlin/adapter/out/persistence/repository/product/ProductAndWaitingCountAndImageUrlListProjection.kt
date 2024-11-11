package org.team_alilm.adapter.out.persistence.repository.product

import org.team_alilm.adapter.out.persistence.entity.ProductJpaEntity

data class ProductAndWaitingCountAndImageUrlListProjection(
    val productJpaEntity: ProductJpaEntity,
    val waitingCount: Long,
    val imageUrlList: Any?
)