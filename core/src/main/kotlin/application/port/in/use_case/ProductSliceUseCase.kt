package org.team_alilm.application.port.`in`.use_case

import org.team_alilm.application.port.out.LoadProductSlicePort.ProductAndWaitingCount

interface ProductSliceUseCase {

    fun productSlice(command: ProductSliceCommand): CustomSlice

    data class CustomSlice(
        val contents: List<ProductSliceResult>,
        val hasNext: Boolean,
        val isLast: Boolean,
        val number: Int,
        val size: Int
    )

    data class ProductSliceCommand(
        val page: Int,
        val size: Int
    )

    data class ProductSliceResult(
        val id: Long,
        val number: Long,
        val name: String,
        val brand: String,
        val thumbnailUrl: String,
        val imageUrl: String,
        val store: String,
        val price: Int,
        val firstCategory: String,
        val secondCategory: String?,
        val firstOption: String,
        val secondOption: String?,
        val thirdOption: String?,
        val waitingCount: Long
    ) {

        companion object {
            fun from (basketAndCountProjection: ProductAndWaitingCount): ProductSliceResult {
                return ProductSliceResult(
                    id = basketAndCountProjection.product.id!!.value,
                    number = basketAndCountProjection.product.number,
                    name = basketAndCountProjection.product.name,
                    brand = basketAndCountProjection.product.brand,
                    thumbnailUrl = basketAndCountProjection.product.thumbnailUrl,
                    imageUrl = basketAndCountProjection.product.thumbnailUrl,
                    store = basketAndCountProjection.product.store.name,
                    price = basketAndCountProjection.product.price,
                    firstCategory = basketAndCountProjection.product.firstCategory,
                    secondCategory = basketAndCountProjection.product.secondCategory,
                    firstOption = basketAndCountProjection.product.firstOption,
                    secondOption = basketAndCountProjection.product.secondOption,
                    thirdOption = basketAndCountProjection.product.thirdOption,
                    waitingCount = basketAndCountProjection.waitingCount
                )
            }
        }
    }
}