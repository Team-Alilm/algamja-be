package org.teamalilm.alilmbe.adapter.out.persistence

import org.teamalilm.alilmbe.adapter.out.persistence.entity.product.ProductJpaEntity
import org.teamalilm.alilmbe.application.port.out.AddProductPort
import org.teamalilm.alilmbe.adapter.out.persistence.repository.SpringDataProductRepository

class ProductPersistenceAdapter(
    private val springDataProductRepository: SpringDataProductRepository
) : AddProductPort {

    override fun invoke(productJpaEntity: ProductJpaEntity): ProductJpaEntity {
        return springDataProductRepository.save(productJpaEntity)
    }

}