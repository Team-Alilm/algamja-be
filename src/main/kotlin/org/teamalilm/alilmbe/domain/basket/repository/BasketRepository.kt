package org.teamalilm.alilmbe.domain.basket.repository

import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.teamalilm.alilmbe.domain.basket.entity.Basket
import org.teamalilm.alilmbe.domain.member.entity.Member
import org.teamalilm.alilmbe.domain.product.entity.Product

interface BasketRepository : JpaRepository<Basket, Long> {

    fun findAllByProductId(productId: Long): List<Basket>

    @Query(
        """
            SELECT b
            FROM Basket b 
            WHERE b.id IN (
                SELECT MIN(b3.id)
                FROM Basket b3
                GROUP BY b3.product.id
            )
"""
    )
    fun findDistinctByProductAndOldestCreationTimeWithCount(pageable: Pageable): Slice<BasketCountProjection>

    fun existsByMemberAndProduct(member: Member, product: Product): Boolean

    @Query("SELECT b FROM Basket b WHERE b.member = :member")
    fun findAllByMember(member: Member): List<Basket>


    interface BasketCountProjection {
        fun getBasket(): Basket
        fun getCount(): Long
    }
}
