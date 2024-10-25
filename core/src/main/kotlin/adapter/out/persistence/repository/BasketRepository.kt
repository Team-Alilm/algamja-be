package org.team_alilm.adapter.out.persistence.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.team_alilm.adapter.out.persistence.entity.BasketJpaEntity
import org.team_alilm.adapter.out.persistence.repository.basket.BasketAndProductProjection

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

}
