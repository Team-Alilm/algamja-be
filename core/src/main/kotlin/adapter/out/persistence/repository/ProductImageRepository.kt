package org.team_alilm.adapter.out.persistence.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.team_alilm.adapter.out.persistence.entity.ProductImageJpaEntity

interface ProductImageRepository : JpaRepository<ProductImageJpaEntity, Long> {

    @Query(
        value = """
        INSERT IGNORE INTO product_image (product_number, image_url)
        VALUES 
        :productImages
    """,
        nativeQuery = true
    )
    fun saveAllIgnore(productImages: List<ProductImageJpaEntity>)

}
