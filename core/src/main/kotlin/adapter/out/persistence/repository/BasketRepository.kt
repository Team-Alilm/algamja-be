package org.team_alilm.adapter.out.persistence.repository

import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Slice
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.team_alilm.adapter.out.persistence.entity.BasketJpaEntity

interface BasketRepository : JpaRepository<BasketJpaEntity, Long> {

    @Query("""
    SELECT 
        p.id, 
        p.number, 
        p.brand, 
        p.image_url, 
        p.store, 
        p.category, 
        p.price, 
        p.first_option, 
        p.second_option, 
        p.third_option, 
        p.is_delete, 
        p.created_date, 
        p.last_modified_date, 
        p.name, 
        COUNT(b.id) AS waiting_count
    FROM 
        product p
    LEFT JOIN 
        basket b ON p.id = b.product_id AND b.is_delete = false
    WHERE 
        p.is_delete = false
    GROUP BY 
        p.id, 
        p.number, 
        p.brand, 
        p.image_url, 
        p.store, 
        p.category, 
        p.price, 
        p.first_option, 
        p.second_option, 
        p.third_option, 
        p.is_delete, 
        p.created_date, 
        p.last_modified_date, 
        p.name
    ORDER BY 
        waiting_count DESC
    LIMIT :#{#pageRequest.pageSize} OFFSET :#{#pageRequest.offset}
""", nativeQuery = true)
    fun loadBasketSlice(pageRequest: PageRequest): Slice<ProductAndWaitingCount>


    @Query("""
    SELECT 
        p.id AS productId, 
        p.number AS productNumber, 
        p.brand AS productBrand, 
        p.image_url AS productImageUrl, 
        p.store AS productStore, 
        p.category AS productCategory, 
        p.price AS productPrice, 
        p.first_option AS productFirstOption, 
        p.second_option AS productSecondOption, 
        p.third_option AS productThirdOption, 
        p.is_delete AS productIsDelete, 
        p.created_date AS productCreatedDate, 
        p.last_modified_date AS productLastModifiedDate, 
        p.name AS productName, 
        b.id,
        COUNT(b.id) AS productCount
    FROM 
        basket b
    JOIN 
        product p ON b.product_id = p.id
    WHERE 
        b.member_id = :memberId
        AND b.is_delete = false
        AND p.is_delete = false
        AND b.is_alilm = false
    GROUP BY 
        p.id, 
        p.number, 
        p.brand, 
        p.image_url, 
        p.store, 
        p.category, 
        p.price, 
        p.first_option, 
        p.second_option, 
        p.third_option, 
        p.is_delete, 
        p.created_date, 
        p.last_modified_date, 
        p.name,
        b.id
""", nativeQuery = true)
    fun myBasketList(memberId: Long): List<ProductAndWaitingCount>


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

    data class ProductAndWaitingCount(
        val productId: Long,
        val productNumber: Long,
        val productBrand: String,
        val productImageUrl: String,
        val productStore: String,
        val productCategory: String,
        val productPrice: Int,
        val productFirstOption: String,
        val productSecondOption: String?,
        val productThirdOption: String?,
        val productIsDelete: Boolean,
        val productCreatedDate: Long,
        val productLastModifiedDate: Long,
        val productName: String,
        val waitingCount: Long
    )

}

