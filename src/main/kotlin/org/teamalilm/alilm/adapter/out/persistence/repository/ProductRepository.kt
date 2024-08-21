package org.teamalilm.alilm.adapter.out.persistence.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.teamalilm.alilm.adapter.out.persistence.entity.ProductJpaEntity
import org.teamalilm.alilm.domain.Product

interface ProductRepository : JpaRepository<ProductJpaEntity, Long> {

    fun findByNumberAndStoreAndFirstOptionAndSecondOptionAndThirdOption(
        number: Long,
        store: Product.Store,
        firstOption: String,
        secondOption: String?,
        thirdOption: String?,
    ): ProductJpaEntity?

}