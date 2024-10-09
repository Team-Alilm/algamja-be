package org.team_alilm.adapter.out.persistence.adapter

import org.springframework.stereotype.Component
import org.teamalilm.alilm.adapter.out.persistence.mapper.ProductMapper
import org.teamalilm.alilm.adapter.out.persistence.repository.ProductRepository
import org.teamalilm.alilm.adapter.out.persistence.repository.spring_data.SpringDataProductRepository
import org.teamalilm.alilm.application.port.out.AddProductPort
import org.teamalilm.alilm.application.port.out.LoadProductPort
import org.teamalilm.alilm.domain.Product

@Component
class ProductAdapter(
    private val springDataProductRepository: SpringDataProductRepository,
    private val productRepository: ProductRepository,
    private val productMapper: ProductMapper,
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
        firstOption: String,
        secondOption: String?,
        thirdOption: String?,
    ): Product? {
        val productJpaEntity = productRepository.findByNumberAndStoreAndFirstOptionAndSecondOptionAndThirdOption(
            number = number,
            store = store,
            firstOption = firstOption,
            secondOption = secondOption,
            thirdOption = thirdOption
        )

        return productMapper.mapToDomainEntityOrNull(productJpaEntity)
    }

    override fun loadProduct(productId: Product.ProductId): Product? {
        val productJpaEntity = springDataProductRepository.findById(productId.value).orElse(null)

        return productMapper.mapToDomainEntityOrNull(productJpaEntity)
    }

}