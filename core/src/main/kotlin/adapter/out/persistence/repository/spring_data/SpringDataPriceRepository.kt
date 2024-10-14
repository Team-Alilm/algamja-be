package org.team_alilm.adapter.out.persistence.repository.spring_data

import org.springframework.data.jpa.repository.JpaRepository
import org.team_alilm.adapter.out.persistence.entity.PriceJpaEntity

interface SpringDataPriceRepository : JpaRepository<PriceJpaEntity, Long> {
    fun findAllByProductJpaEntityIdAndIsDeleteFalseOrderByCreatedDateDesc(
        productJpaEntityId: Long
    ): List<PriceJpaEntity>?
}

