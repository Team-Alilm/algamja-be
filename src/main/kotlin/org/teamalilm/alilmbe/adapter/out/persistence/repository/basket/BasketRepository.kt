package org.teamalilm.alilmbe.adapter.out.persistence.repository.basket

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.teamalilm.alilmbe.adapter.out.persistence.entity.basket.BasketJpaEntity
import org.teamalilm.alilmbe.domain.member.Member
import org.teamalilm.alilmbe.domain.product.Product

interface BasketRepository : JpaRepository<BasketJpaEntity, Long> {

    @Query(
        """
        SELECT b 
        FROM BasketJpaEntity b
        WHERE b.member = :member
        AND b.product = :product
        """
    )
    fun findByMemberIdAndProductId(
        @Param("memberId") memberId: Member.MemberId,
        @Param("productId") productId: Product.ProductId
    ): BasketJpaEntity?

}
