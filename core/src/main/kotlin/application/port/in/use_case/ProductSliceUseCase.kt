package org.team_alilm.application.port.`in`.use_case

import domain.product.Product

interface ProductSliceUseCase {

    fun productSlice(command: ProductSliceCommand): CustomSlice

    data class CustomSlice(
        val contents: List<ProductSliceResult>,
        val hasNext: Boolean,
        val size: Int
    )

    data class ProductSliceCommand(
        val size: Int,
        val category: String?,
        val sort: String,
        val lastProductId: Long?,
        val waitingCount: Long?,
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
                    number = product.number,
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