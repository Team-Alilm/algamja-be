package org.teamalilm.alilmbe.domain.basket.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.teamalilm.alilmbe.domain.basket.entity.Basket
import org.teamalilm.alilmbe.domain.member.entity.Member
import org.teamalilm.alilmbe.domain.product.entity.Product

interface BasketRepository : JpaRepository<Basket, Long> {

    fun findAllByProductId(productId: Long): List<Basket>

    fun existsByMemberAndProduct(member: Member, product: Product): Boolean

    @Query("SELECT b FROM Basket b WHERE b.member = :member")
    fun findAllByMember(member: Member): List<Basket>

}
