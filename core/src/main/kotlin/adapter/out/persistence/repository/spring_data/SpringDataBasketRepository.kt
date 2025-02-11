package org.team_alilm.adapter.out.persistence.repository.spring_data

import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Slice
import org.springframework.data.jpa.repository.JpaRepository
import org.team_alilm.adapter.out.persistence.entity.BasketJpaEntity

interface SpringDataBasketRepository : JpaRepository<BasketJpaEntity, Long> {

    fun findByMemberIdAndProductId(
        memberId: Long,
        productId: Long
    ): BasketJpaEntity?

    fun findByIdAndMemberId(basketId: Long, memberId: Long): BasketJpaEntity?

    fun findByMemberIdAndIsDeleteFalse(memberId: Long): List<BasketJpaEntity>

    fun findByProductIdAndIsDeleteFalseAndIsAlilmFalse(productId: Long): List<BasketJpaEntity>

    fun findByMemberIdAndProductIdAndIsDelete(memberId: Long, productId: Long, isDelete: Boolean): BasketJpaEntity?

    fun countByProductIdAndIsAlilmFalseAndIsDeleteFalse(productId: Long): Long

    fun findAllByProductIdAndIsAlilmFalseAndIsDeleteFalse(productId: Long): List<BasketJpaEntity>

    fun findAllByIsDeleteFalseAndIsAlilmFalseOrderByCountDesc(pageRequest: PageRequest): Slice<BasketJpaEntity>
}