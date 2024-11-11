package org.team_alilm.adapter.out.persistence.adapter

import org.slf4j.LoggerFactory
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Slice
import org.springframework.stereotype.Component
import org.team_alilm.adapter.out.persistence.mapper.ProductMapper
import org.team_alilm.adapter.out.persistence.repository.ProductRepository
import org.team_alilm.adapter.out.persistence.repository.spring_data.SpringDataProductRepository
import org.team_alilm.application.port.out.AddProductPort
import org.team_alilm.application.port.out.LoadCrawlingProductsPort
import org.team_alilm.application.port.out.LoadProductPort
import org.team_alilm.application.port.out.LoadProductPort.*
import org.team_alilm.application.port.out.LoadProductSlicePort
import org.team_alilm.domain.product.Product
import org.team_alilm.domain.product.ProductId
import org.team_alilm.domain.product.Store
import org.team_alilm.global.error.NotFoundProductException

@Component
class ProductAdapter(
    private val springDataProductRepository: SpringDataProductRepository,
    private val productRepository: ProductRepository,
    private val productMapper: ProductMapper,
) : AddProductPort,
    LoadProductPort,
    LoadCrawlingProductsPort,
    LoadProductSlicePort {

    private val log = LoggerFactory.getLogger(ProductAdapter::class.java)

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
        store: Store,
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

    override fun loadProduct(productId: ProductId): Product? {
        val productJpaEntity = springDataProductRepository.findByIdAndIsDeleteFalse(productId.value)

        return productMapper.mapToDomainEntityOrNull(productJpaEntity)
    }

    override fun loadRecentProduct(): List<Product> {
        return productRepository.findRecentProducts().map {
            productMapper.mapToDomainEntity(it)
        }
    }

    override fun loadRelatedProduct(category: String): List<Product> {
        return springDataProductRepository.findTop10ByCategoryAndIsDeleteFalseOrderByCreatedDate(category).map {
            productMapper.mapToDomainEntity(it)
        }
    }

    override fun loadProductDetails(productId: ProductId): ProductAndWaitingCountAndImageList? {
        val projectionList = productRepository.findByDetails(productId.value)

        return ProductAndWaitingCountAndImageList.of(
            product = productMapper.mapToDomainEntityOrNull(projectionList.productJpaEntity) ?: throw NotFoundProductException(),
            waitingCount = projectionList.waitingCount,
            imageUrlList = projectionList.imageUrlList.toString().split(",") ?: emptyList()
        )
    }

    override fun related(category: String): List<Product> {
        TODO("Not yet implemented")
    }

    override fun loadCrawlingProducts(): List<Product> {
        return try {
            productRepository.findCrawlingProducts().map {
                productMapper.mapToDomainEntity(it)
            }
        } catch (e: Exception) {
            log.error("Failed to load products in baskets", e)
            emptyList()
        }
    }

    override fun loadProductSlice(pageRequest: PageRequest): Slice<LoadProductSlicePort.ProductAndWaitingCount> {
        return productRepository.findAllProductSlice(pageRequest).map { LoadProductSlicePort.ProductAndWaitingCount.of(
            product = productMapper.mapToDomainEntity(it.productJpaEntity),
            waitingCount = it.waitingCount
        ) }
    }

}