package org.teamalilm.alilmbe.adapter.out.persistence.repository.product

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.teamalilm.alilmbe.adapter.out.persistence.entity.basket.BasketJpaEntity
import org.teamalilm.alilmbe.adapter.out.persistence.entity.member.Member
import org.teamalilm.alilmbe.adapter.out.persistence.entity.product.ProductJpaEntity

interface BasketRepository : JpaRepository<BasketJpaEntity, Long> {

    @Query("SELECT b FROM BasketJpaEntity b WHERE b.member = :member")
    fun findAllByMember(member: Member): List<BasketJpaEntity>

}
