package org.teamalilm.alilmbe.adapter.out.persistence.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.teamalilm.alilmbe.adapter.out.persistence.entity.basket.Basket
import org.teamalilm.alilmbe.adapter.out.persistence.entity.member.Member
import org.teamalilm.alilmbe.adapter.out.persistence.entity.product.Product

interface BasketRepository : JpaRepository<Basket, Long> {

    fun findAllByProductId(productId: Long): List<Basket>

    fun existsByMemberAndProduct(member: Member, product: Product): Boolean

    @Query("SELECT b FROM Basket b WHERE b.member = :member")
    fun findAllByMember(member: Member): List<Basket>

}
