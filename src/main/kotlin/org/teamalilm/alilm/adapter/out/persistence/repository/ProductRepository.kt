package org.teamalilm.alilm.adapter.out.persistence.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.teamalilm.alilm.adapter.out.persistence.entity.ProductJpaEntity
import org.teamalilm.alilm.domain.Product

interface ProductRepository : JpaRepository<ProductJpaEntity, Long> {

    fun findByNumberAndStoreAndOption1AndOption2AndOption3(
        number: Long,
        store: Product.Store,
        option1: String,
        option2: String?,
        option3: String?,
    ): ProductJpaEntity?

}