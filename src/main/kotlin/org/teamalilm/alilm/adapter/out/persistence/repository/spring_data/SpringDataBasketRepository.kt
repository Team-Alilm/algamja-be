package org.teamalilm.alilm.adapter.out.persistence.repository.spring_data

import org.springframework.data.jpa.repository.JpaRepository
import org.teamalilm.alilm.adapter.out.persistence.entity.BasketJpaEntity
import org.teamalilm.alilm.adapter.out.persistence.entity.MemberJpaEntity

interface SpringDataBasketRepository : JpaRepository<BasketJpaEntity, Long> {

    fun findByMemberJpaEntityIdAndProductJpaEntityIdAndIsDeleteFalse(
        memberJpaEntityId: Long,
        productJpaEntityId: Long
    ): BasketJpaEntity?

    fun findAllByMemberJpaEntityAndIsDeleteFalse(mapToJpaEntity: MemberJpaEntity) : List<BasketJpaEntity>

    fun findAllByIsDeleteFalse(): List<BasketJpaEntity>

    fun findByIsAlilmTrue() : List<BasketJpaEntity>

    fun findByIsAlilmTrueAndAlilmDateGreaterThanEqual(midnightMillis: Long): List<BasketJpaEntity>
}

