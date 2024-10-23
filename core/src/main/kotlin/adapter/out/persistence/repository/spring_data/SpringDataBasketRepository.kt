package org.team_alilm.adapter.out.persistence.repository.spring_data

import org.springframework.data.jpa.repository.JpaRepository
import org.team_alilm.adapter.out.persistence.entity.BasketJpaEntity

interface SpringDataBasketRepository : JpaRepository<BasketJpaEntity, Long> {

    fun findByMemberIdAndIsDeleteFalseAndProductId(
        memberId: Long,
        productId: Long
    ): BasketJpaEntity?

    fun findByIsAlilmTrueAndIsDeleteFalse() : List<BasketJpaEntity>

    fun findByIsAlilmTrueAndAlilmDateGreaterThanEqualAndIsDeleteFalse(midnightMillis: Long): List<BasketJpaEntity>

    fun findByIdAndMemberId(basketId: Long, memberId: Long): BasketJpaEntity?

    fun findByMemberIdAndIsDeleteFalse(memberId: Long): List<BasketJpaEntity>

    fun findByProductIdAndIsDeleteFalseAndIsAlilmFalse(productId: Long): List<BasketJpaEntity>
}