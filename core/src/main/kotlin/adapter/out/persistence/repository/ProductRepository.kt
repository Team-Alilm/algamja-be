package org.team_alilm.adapter.out.persistence.repository

import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Slice
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.team_alilm.adapter.out.persistence.entity.ProductJpaEntity
import org.team_alilm.adapter.out.persistence.repository.product.ProductAndWaitingCountProjection
import org.team_alilm.domain.Product

interface ProductRepository : JpaRepository<ProductJpaEntity, Long> {

    fun findByNumberAndStoreAndFirstOptionAndSecondOptionAndThirdOption(
        number: Long,
        store: Product.Store,
        firstOption: String,
        secondOption: String?,
        thirdOption: String?,
    ): ProductJpaEntity?

    @Query(value = """
        SELECT
            p
        FROM
            ProductJpaEntity p
        JOIN
            BasketJpaEntity b
        ON 
            b.productId = p.id
        and b.isAlilm = false
        and p.isDelete = false
        and b.isDelete = false
        GROUP BY p.number
    """)
    fun findCrawlingProducts(): List<ProductJpaEntity>

    @Query("""
        SELECT 
            new org.team_alilm.adapter.out.persistence.repository.product.ProductAndWaitingCountProjection(p, COUNT(b))
        FROM 
            ProductJpaEntity p
        left JOIN 
            BasketJpaEntity b
        ON 
            b.productId = p.id
        and b.isDelete = false
        and p.isDelete = false
        group by b.productId
        order by COUNT(b) desc, p.id desc
    """)
    fun findAllProductSlice(pageRequest: PageRequest): Slice<ProductAndWaitingCountProjection>

    @Query("""
        SELECT 
            new org.team_alilm.adapter.out.persistence.repository.product.ProductAndWaitingCountProjection(p, COUNT(b)) 
        FROM 
            ProductJpaEntity p 
        JOIN 
            BasketJpaEntity b 
        ON 
            b.productId = p.id
        and b.isDelete = false
        and p.isDelete = false
        and p.id = :value
    """)
    fun findByIdAndIsDeleteFalseAndWaitingCount(value: Long): ProductAndWaitingCountProjection?

    @Query("""
        SELECT 
            p
        FROM 
            ProductJpaEntity p 
        JOIN 
            AlilmJpaEntity a
            on a.productId = p.id
        WHERE 
            p.isDelete = false
        and a.productId = p.id
        group by p.id
        ORDER BY 
            a.createdDate DESC
        LIMIT 10
    """)
    fun findRecentProducts (): List<ProductJpaEntity>
}

