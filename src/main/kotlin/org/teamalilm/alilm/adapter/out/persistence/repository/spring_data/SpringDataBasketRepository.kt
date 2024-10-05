package org.teamalilm.alilm.adapter.out.persistence.repository.spring_data

import org.springframework.data.jpa.repository.JpaRepository
import org.teamalilm.alilm.adapter.out.persistence.entity.BasketJpaEntity
import org.teamalilm.alilm.adapter.out.persistence.entity.MemberJpaEntity

interface SpringDataBasketRepository : JpaRepository<BasketJpaEntity, Long> {

    fun findByMemberJpaEntityIdAndIsDeleteFalseAndProductJpaEntityId(
        memberJpaEntityId: Long,
        productJpaEntityId: Long
    ): BasketJpaEntity?

    fun findAllByMemberJpaEntityAndIsDeleteFalseOrderByCreatedDateDesc(mapToJpaEntity: MemberJpaEntity) : List<BasketJpaEntity>

    fun findAllByIsDeleteFalseAndIsAlilmFalse(): List<BasketJpaEntity>

    fun findByIsAlilmTrueAndIsDeleteFalse() : List<BasketJpaEntity>

    fun findByIsAlilmTrueAndAlilmDateGreaterThanEqualAndIsDeleteFalse(midnightMillis: Long): List<BasketJpaEntity>

    fun findByMemberJpaEntityIdAndIsDeleteFalseAndIsAlilmTrue(memberJpaEntityId: Long): List<BasketJpaEntity>
}

