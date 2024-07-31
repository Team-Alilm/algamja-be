package org.teamalilm.alilmbe.adapter.out.persistence.repository

import jakarta.persistence.Tuple
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Slice
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.teamalilm.alilmbe.adapter.out.persistence.entity.BasketJpaEntity

interface BasketRepository : JpaRepository<BasketJpaEntity, Long> {

    @Query(
        """
            SELECT p as productJpaEntity, COUNT(b) as waitingCount
            FROM BasketJpaEntity b
            JOIN b.productJpaEntity p
            GROUP BY p.id
            ORDER BY COUNT(b) DESC
        """
    )
    fun loadBasketSlice(pageRequest: PageRequest): Slice<Tuple>

}

