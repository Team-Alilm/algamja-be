package org.teamalilm.alilmbe.adapter.out.persistence.adapter

import org.springframework.stereotype.Component
import org.teamalilm.alilmbe.adapter.out.persistence.mapper.ProductMapper
import org.teamalilm.alilmbe.adapter.out.persistence.repository.product.SpringDataProductRepository
import org.teamalilm.alilmbe.application.port.out.AddProductPort
import org.teamalilm.alilmbe.domain.product.Product

@Component
class AddProductPersistenceAdapter(
    private val springDataProductRepository: SpringDataProductRepository,
    private val productMapper: ProductMapper
) : AddProductPort {

    override fun addProduct(product: Product) {
        springDataProductRepository.save(productMapper.mapToJpaEntity(product))
    }

}