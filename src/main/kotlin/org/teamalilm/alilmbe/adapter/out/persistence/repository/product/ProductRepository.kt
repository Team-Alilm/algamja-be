package org.teamalilm.alilmbe.adapter.out.persistence.repository.product

import org.springframework.data.jpa.repository.JpaRepository
import org.teamalilm.alilmbe.adapter.out.persistence.entity.product.ProductJpaEntity
import org.teamalilm.alilmbe.adapter.out.persistence.entity.product.Store

interface ProductRepository : JpaRepository<ProductJpaEntity, Long> {

    fun existsByNumberAndStoreAndOption1AndOption2AndOption3(
        number: Long,
        store: Store,
        option1: String,
        option2: String?,
        option3: String?,
    ) : Boolean

    fun findByNumberAndStoreAndOption1AndOption2AndOption3(
        number: Long,
        store: Store,
        option1: String,
        option2: String?,
        option3: String?,
    ): ProductJpaEntity?

}