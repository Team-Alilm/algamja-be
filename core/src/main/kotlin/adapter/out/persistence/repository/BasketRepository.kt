package org.team_alilm.adapter.out.persistence.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.team_alilm.adapter.out.persistence.entity.BasketJpaEntity
import org.team_alilm.adapter.out.persistence.entity.ProductJpaEntity

interface BasketRepository : JpaRepository<BasketJpaEntity, Long> {

    @Query("""
        select BasketJpaEntity, ProductJpaEntity, COUNT(b.id) as waitingCount
        from BasketJpaEntity b
        join ProductJpaEntity p on b.productId = p.id
        where b.memberId = :memberId
        and b.isDelete = false
        and p.isDelete = false
        group by p.id
        order by b.createdDate
    """)
    fun myBasketList(memberId: Long): List<BasketAndProduct>

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

    data class BasketAndProduct(
        val basketJpaEntity: BasketJpaEntity,
        val productJpaEntity: ProductJpaEntity,
        val waitingCount: Long
    )

}
