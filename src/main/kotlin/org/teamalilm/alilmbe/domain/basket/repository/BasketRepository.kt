package org.teamalilm.alilmbe.domain.basket.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.teamalilm.alilmbe.domain.basket.entity.Basket
import org.teamalilm.alilmbe.domain.member.entity.Member
import org.teamalilm.alilmbe.domain.product.entity.Product

interface BasketRepository : JpaRepository<Basket, Long> {
    fun existsByProductAndMember(product: Product, member: Member): Boolean

    fun deleteByProductIdIn(productIds: List<Long>): Long
    fun findByProductId(productId: Long): Basket?

    @Query("SELECT b FROM Basket b GROUP BY b.product.id")
    fun findAllByGroupByProductId(): List<Basket>
}