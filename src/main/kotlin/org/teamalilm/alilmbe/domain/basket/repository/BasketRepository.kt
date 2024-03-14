package org.teamalilm.alilmbe.domain.basket.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.teamalilm.alilmbe.domain.basket.entity.Basket
import org.teamalilm.alilmbe.domain.member.entity.Member
import org.teamalilm.alilmbe.domain.product.entity.Product

interface BasketRepository : JpaRepository<Basket, Long> {
    fun existsByProductAndMember(product: Product, member: Member): Boolean

}