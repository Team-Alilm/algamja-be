package org.teamalilm.alilmbe.adapter.out.persistence.adapter

import org.springframework.stereotype.Component
import org.teamalilm.alilmbe.adapter.out.persistence.entity.product.Store
import org.teamalilm.alilmbe.adapter.out.persistence.mapper.ProductMapper
import org.teamalilm.alilmbe.adapter.out.persistence.repository.product.ProductRepository
import org.teamalilm.alilmbe.adapter.out.persistence.repository.product.SpringDataProductRepository
import org.teamalilm.alilmbe.application.port.out.product.AddProductPort
import org.teamalilm.alilmbe.application.port.out.product.LoadProductPort
import org.teamalilm.alilmbe.domain.product.Product

@Component
class ProductPersistenceAdapter(
    private val springDataProductRepository: SpringDataProductRepository,
    private val productRepository: ProductRepository,
    private val productMapper: ProductMapper
) : AddProductPort, LoadProductPort {

    override fun invoke(product: Product) : Product {
        return productMapper
            .mapToDomainEntity(
                springDataProductRepository.save(
                    productMapper.mapToJpaEntity(product)
                )
            )
    }

    override fun loadProduct(
        number: Long,
        store: Store,
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

        return productMapper.mapToDomainEntity(productJpaEntity)
    }


}