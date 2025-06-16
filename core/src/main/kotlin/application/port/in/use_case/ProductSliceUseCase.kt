package org.team_alilm.application.port.`in`.use_case

import domain.product.Product
import org.team_alilm.domain.product.ProductCategory
import org.team_alilm.domain.product.ProductSortType

interface ProductSliceUseCase {

    fun productSlice(command: ProductSliceCommand): CustomSlice

    data class CustomSlice(
        val contents: List<ProductSliceResult>,
        val hasNext: Boolean,
        val size: Int
    )

    data class ProductSliceCommand(
        val size: Int,
        val category: ProductCategory?,
        val sort: ProductSortType,
        val lastProductId: Long?,
        val waitingCount: Long?,
        val price: Int?
    )

    data class ProductSliceResult(
        val id: Long,
        val number: Long,
        val name: String,
        val brand: String,
        val thumbnailUrl: String,
        val store: String,
        val price: Int,
        val firstCategory: String,
        val secondCategory: String?,
        val firstOption: String?,
        val secondOption: String?,
        val thirdOption: String?,
        val waitingCount: Long
    ) {

        companion object {
            fun from (product: Product, waitingCount: Long): ProductSliceResult {
                return ProductSliceResult(
                    id = product.id!!,
                    number = product.storeNumber,
                    name = product.name,
                    brand = product.brand,
                    thumbnailUrl = product.thumbnailUrl,
                    store = product.store.name,
                    price = product.price,
                    firstCategory = product.firstCategory,
                    secondCategory = product.secondCategory,
                    firstOption = product.firstOption,
                    secondOption = product.secondOption,
                    thirdOption = product.thirdOption,
                    waitingCount = waitingCount
                )
            }
        }
    }
}