package org.team_alilm.adapter.out.persistence.repository.spring_data

import org.springframework.data.jpa.repository.JpaRepository
import org.team_alilm.adapter.out.persistence.entity.BasketJpaEntity

interface SpringDataBasketRepository : JpaRepository<BasketJpaEntity, Long> {

    fun findByMemberJpaEntityIdAndIsDeleteFalseAndProductJpaEntityId(
        memberJpaEntityId: Long,
        productJpaEntityId: Long
    ): BasketJpaEntity?

    fun findByIsAlilmTrueAndIsDeleteFalse() : List<BasketJpaEntity>

    fun findByIsAlilmTrueAndAlilmDateGreaterThanEqualAndIsDeleteFalse(midnightMillis: Long): List<BasketJpaEntity>

    fun findByIdAndMemberJpaEntityId(basketId: Long, memberId: Long): BasketJpaEntity?

    fun findByMemberJpaEntityIdAndIsDeleteFalse(memberJpaEntityId: Long): List<BasketJpaEntity>

    fun findByProductJpaEntityIdAndIsDeleteFalseAndIsAlilmFalse(productJpaEntityId: Long): List<BasketJpaEntity>
}