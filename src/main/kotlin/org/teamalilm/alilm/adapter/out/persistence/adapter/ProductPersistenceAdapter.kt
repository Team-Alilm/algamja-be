package org.teamalilm.alilm.adapter.out.persistence.adapter

import org.springframework.stereotype.Component
import org.teamalilm.alilm.adapter.out.persistence.mapper.ProductMapper
import org.teamalilm.alilm.adapter.out.persistence.repository.ProductRepository
import org.teamalilm.alilm.adapter.out.persistence.repository.spring_data.SpringDataProductRepository
import org.teamalilm.alilm.application.port.out.AddProductPort
import org.teamalilm.alilm.application.port.out.LoadProductPort
import org.teamalilm.alilm.domain.Product

@Component
class ProductPersistenceAdapter(
    private val springDataProductRepository: SpringDataProductRepository,
    private val productRepository: ProductRepository,
    private val productMapper: ProductMapper
) : AddProductPort, LoadProductPort {

    override fun addProduct(product: Product) : Product {
        return productMapper
            .mapToDomainEntity(
                springDataProductRepository.save(
                    productMapper.mapToJpaEntity(product)
                )
            )
    }

    override fun loadProduct(
        number: Long,
        store: Product.Store,
        option1: String,
        option2: String?,
        option3: String?,
    ): Product? {
        val productJpaEntity = productRepository.findByNumberAndStoreAndOption1AndOption2AndOption3(
            number = number,
            store = store,
            option1 = option1,
            option2 = option2,
            option3 = option3
        )

        return productMapper.mapToDomainEntityOrNull(productJpaEntity)
    }

    override fun loadProduct(productId: Product.ProductId): Product? {
        val productJpaEntity = springDataProductRepository.findById(productId.value).orElse(null)

        return productMapper.mapToDomainEntityOrNull(productJpaEntity)
    }

}