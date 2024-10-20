package org.team_alilm.adapter.out.persistence.repository.spring_data

import org.springframework.data.jpa.repository.JpaRepository
import org.team_alilm.adapter.out.persistence.entity.BasketJpaEntity
import org.team_alilm.adapter.out.persistence.entity.MemberJpaEntity

interface SpringDataBasketRepository : JpaRepository<BasketJpaEntity, Long> {

    fun findByMemberJpaEntityIdAndIsDeleteFalseAndProductJpaEntityId(
        memberJpaEntityId: Long,
        productJpaEntityId: Long
    ): BasketJpaEntity?

    fun findAllByMemberJpaEntityAndIsDeleteFalseOrderByCreatedDateDesc(mapToJpaEntity: MemberJpaEntity) : List<BasketJpaEntity>

    fun findAllByIsDeleteFalseAndIsAlilmFalse(): List<BasketJpaEntity>

    fun findByIsAlilmTrueAndIsDeleteFalse() : List<BasketJpaEntity>

    fun findByIsAlilmTrueAndAlilmDateGreaterThanEqualAndIsDeleteFalse(midnightMillis: Long): List<BasketJpaEntity>

    fun findByIdAndMemberJpaEntityId(basketId: Long, memberId: Long): BasketJpaEntity?

    fun findByMemberJpaEntityIdAndIsDeleteFalse(memberJpaEntityId: Long): List<BasketJpaEntity>
}