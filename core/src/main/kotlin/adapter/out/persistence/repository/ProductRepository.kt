package org.team_alilm.adapter.out.persistence.repository

import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Slice
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.team_alilm.adapter.out.persistence.entity.ProductJpaEntity
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
        select p.id, p.number, p.brand, p.image_url, p.store, p.category, p.price, 
               p.first_option, p.second_option, p.third_option, p.is_delete, 
               p.created_date, p.last_modified_date, p.name
        from product p
        join basket b on p.id = b.product_id
        where b.is_delete = false and b.is_alilm = false
        group by p.number
        """, nativeQuery = true
    )
    fun findProductsInBaskets(): List<ProductJpaEntity>

    @Query("""
    SELECT new org.team_alilm.adapter.out.persistence.repository.ProductSliceProjection(p, COUNT(b)) 
    FROM ProductJpaEntity p 
    LEFT JOIN BasketJpaEntity b ON b.productId = p.id
    GROUP BY p.id
""")
    fun findAllProductSlice(pageRequest: PageRequest): Slice<ProductSliceProjection>
}

data class ProductSliceProjection(
    val productJpaEntity: ProductJpaEntity,
    val waitingCount: Long
)