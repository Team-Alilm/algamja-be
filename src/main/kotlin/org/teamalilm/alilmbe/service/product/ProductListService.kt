package org.teamalilm.alilmbe.service.product

import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Slice
import org.springframework.stereotype.Service
import org.teamalilm.alilmbe.domain.product.entity.Product.ProductInfo
import org.teamalilm.alilmbe.adapter.out.persistence.repository.ProductRepository
import org.teamalilm.alilmbe.domain.product.repository.impl.ProductCustomRepositoryImpl.*

@Service
class ProductListService(
    private val productRepository: ProductRepository,
) {

    // 상품 전체 조회
    fun listProduct(command: BasketFindAllCommand): Slice<ListProductResult> {
        val query = ProductListQuery(command.pageRequest)

        val productListProjections = productRepository.productList(query)

        return productListProjections.map {
            ListProductResult(
                id = it.id,
                name = it.name,
                brand = it.brand,
                imageUrl = it.imageUrl,
                price = it.price,
                category = it.category,
                productInfo = it.productInfo,
                waitingCount = it.waitingCount,
                oldestCreationTime = it.oldestCreationTime
            )
        }
    }

    data class BasketFindAllCommand(
        val pageRequest: PageRequest
    )

    data class ListProductResult(
        val id: Long,
        val name: String,
        val brand: String,
        val imageUrl: String,
        val price: Int,
        val category: String,
        val productInfo: ProductInfo,
        val waitingCount: Long,
        val oldestCreationTime: Long
    )

}
