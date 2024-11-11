package org.team_alilm.adapter.out.persistence.repository

import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Slice
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.team_alilm.adapter.out.persistence.entity.ProductJpaEntity
import org.team_alilm.adapter.out.persistence.repository.product.ProductAndWaitingCountAndImageUrlListProjection
import org.team_alilm.adapter.out.persistence.repository.product.ProductAndWaitingCountProjection
import org.team_alilm.domain.product.Store

interface ProductRepository : JpaRepository<ProductJpaEntity, Long> {

    fun findByNumberAndStoreAndFirstOptionAndSecondOptionAndThirdOption(
        number: Long,
        store: Store,
        firstOption: String,
        secondOption: String?,
        thirdOption: String?,
    ): ProductJpaEntity?

    @Query(value = """
        SELECT
             distinct p
        FROM
            ProductJpaEntity p
        JOIN
            BasketJpaEntity b
        ON 
            b.productId = p.id
        and b.isAlilm = false
        and p.isDelete = false
        and b.isDelete = false
    """)
    fun findCrawlingProducts(): List<ProductJpaEntity>

    @Query("""
    SELECT 
        new org.team_alilm.adapter.out.persistence.repository.product.ProductAndWaitingCountProjection(
            p, 
            COUNT(CASE WHEN b.isAlilm = false THEN 1 END)
        )
    FROM 
        ProductJpaEntity p
    LEFT JOIN 
        BasketJpaEntity b 
    ON 
        b.productId = p.id
    AND 
        b.isDelete = false
    WHERE 
        p.isDelete = false
    GROUP BY 
        p.id
    ORDER BY 
        COUNT(CASE WHEN b.isAlilm = false THEN 1 END) DESC, p.id DESC
""")
    fun findAllProductSlice(pageRequest: PageRequest): Slice<ProductAndWaitingCountProjection>

    @Query("""
    SELECT 
        new org.team_alilm.adapter.out.persistence.repository.product.ProductAndWaitingCountAndImageUrlListProjection(
            p,
            COUNT(b),
            GROUP_CONCAT(pi.imageUrl)
        )
    FROM 
        ProductJpaEntity p
    LEFT JOIN 
        BasketJpaEntity b ON b.productId = p.id AND b.isDelete = false
    LEFT JOIN 
        ProductImageJpaEntity pi ON pi.productNumber = p.number AND pi.productStore = p.store
    WHERE 
        p.id = :productId AND p.isDelete = false
    GROUP BY 
        p.id
    order by pi.createdDate
""")
    fun findByDetails(productId: Long): ProductAndWaitingCountAndImageUrlListProjection

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

