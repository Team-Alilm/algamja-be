package org.teamalilm.alilmbe.domain.basket.repository

import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Slice
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.teamalilm.alilmbe.domain.basket.entity.Basket
import org.teamalilm.alilmbe.domain.member.entity.Member
import org.teamalilm.alilmbe.domain.product.entity.Product

interface BasketRepository : JpaRepository<Basket, Long> {

    @Query(
        "SELECT COUNT(b), b.product, b.member " +
                "FROM Basket b " +
                "GROUP BY b.product"
    )
    fun findAll(pageRequest: PageRequest): Slice<BasketFindAllQuery>

    data class BasketFindAllQuery(
        val count: Long,
        val product: Product,
        val member: Member
    )
}