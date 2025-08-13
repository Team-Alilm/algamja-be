package org.team_alilm.basket.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.team_alilm.basket.entity.Basket

interface BasketRepository : JpaRepository<Basket, Long> {

    fun findByMemberId(memberId: Long): List<Basket>
    fun countByProductId(productId: Long) : Long
}