package org.team_alilm.adapter.out.persistence.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.team_alilm.adapter.out.persistence.entity.ProductJpaEntity
import org.team_alilm.application.port.out.LoadProductsInBasketsPort
import org.team_alilm.domain.Product

interface ProductRepository : JpaRepository<ProductJpaEntity, Long> {

    fun findByNumberAndStoreAndFirstOptionAndSecondOptionAndThirdOption(
        number: Long,
        store: Product.Store,
        firstOption: String,
        secondOption: String?,
        thirdOption: String?,
    ): ProductJpaEntity?

    @Query(
        """
        select distinct p from ProductJpaEntity p
        join BasketJpaEntity b
        on p.id = b.productJpaEntityId
        where b.isDelete = false
        """
    )
    fun findProductsInBaskets(): List<ProductJpaEntity>

}