package org.teamalilm.alilmbe.domain.basket.repository

import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.teamalilm.alilmbe.domain.basket.entity.Basket

interface BasketRepository : JpaRepository<Basket, Long> {

    @Query(
        "SELECT b as basket, (SELECT COUNT(b2) FROM Basket b2 WHERE b2.product.id = b.product.id) as count " +
                "FROM Basket b " +
                "WHERE b.id " +
                "IN (SELECT MIN(b3.id) FROM Basket b3 GROUP BY b3.product.id)"
    )
    fun findDistinctByProductAndOldestCreationTimeWithCount(pageable: Pageable): Slice<BasketCountProjection>

    interface BasketCountProjection {
        fun getBasket(): Basket
        fun getCount(): Long
    }
}
