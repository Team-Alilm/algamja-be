package org.teamalilm.alilmbe.adapter.out.persistence.repository.basket

import org.springframework.data.jpa.repository.JpaRepository
import org.teamalilm.alilmbe.adapter.out.persistence.entity.basket.BasketJpaEntity
import org.teamalilm.alilmbe.domain.member.Member
import org.teamalilm.alilmbe.domain.product.Product

interface SpringDataBasketRepository : JpaRepository<BasketJpaEntity, Long> {

    fun findByMemberJpaEntityIdAndProductJpaEntityId(
        memberId: Member.MemberId,
        productId: Product.ProductId
    ): BasketJpaEntity?

}

