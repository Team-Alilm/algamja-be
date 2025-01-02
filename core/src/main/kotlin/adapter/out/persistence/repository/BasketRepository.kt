package org.team_alilm.adapter.out.persistence.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.team_alilm.adapter.out.persistence.entity.BasketJpaEntity
import org.team_alilm.adapter.out.persistence.repository.basket.BasketAndMemberProjection
import org.team_alilm.adapter.out.persistence.repository.basket.BasketAndProductProjection
import domain.product.Product

interface BasketRepository : JpaRepository<BasketJpaEntity, Long> {

    @Query("""
    select new org.team_alilm.adapter.out.persistence.repository.basket.BasketAndProductProjection(
        b, 
        p, 
        count(b2)
    )
    from BasketJpaEntity b
    join ProductJpaEntity p on b.productId = p.id
    left join BasketJpaEntity b2 on b2.productId = p.id and b2.isDelete = false
    where b.memberId = :memberId
    and b.isDelete = false
    and p.isDelete = false
    group by b.id, p.id
    order by b.lastModifiedDate desc, b.id desc
""")
    fun myBasketList(memberId: Long): List<BasketAndProductProjection>

    @Query("""
        SELECT 
            b as basketJpaEntity
        FROM
            BasketJpaEntity b
        JOIN
            ProductJpaEntity p
            ON b.productId = p.id
        WHERE
            b.isDelete = false
            AND p.isDelete = false
            AND b.isAlilm = false
            AND p.number = :productNumber
    """)
    fun findByProductNumber(productNumber: Number): List<BasketJpaEntity>

    @Query(
        """
        SELECT 
            new org.team_alilm.adapter.out.persistence.repository.basket.BasketAndMemberProjection(b, m, f)
        FROM
            BasketJpaEntity b
        JOIN
            MemberJpaEntity m
            ON b.memberId = m.id
        JOIN
            ProductJpaEntity p
            ON b.productId = p.id
        JOIN 
            FcmTokenJpaEntity f
            ON m.id = f.memberId
        WHERE
            b.isDelete = false
            AND p.isDelete = false
            AND b.isAlilm = false
            AND b.productId = :productId
            AND f.isDelete = false
            AND m.isDelete = false
        """
    )
    fun findBasketAndMemberByProductNumberAndMemberId(
        productId: Long
    ): List<BasketAndMemberProjection>
}
