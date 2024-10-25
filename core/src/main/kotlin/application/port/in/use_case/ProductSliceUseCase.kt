package org.team_alilm.application.port.`in`.use_case

import org.team_alilm.application.port.out.LoadProductSlicePort

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
        val imageUrl: String,
        val store: String,
        val price: Int,
        val category: String,
        val firstOption: String,
        val secondOption: String?,
        val thirdOption: String?,
        val waitingCount: Long
    ) {

        companion object {
            fun from (basketAndCountProjection: LoadProductSlicePort.ProductAndWaitingCount): ProductSliceResult {
                return ProductSliceResult(
                    id = basketAndCountProjection.product.id!!.value,
                    number = basketAndCountProjection.product.number,
                    name = basketAndCountProjection.product.name,
                    brand = basketAndCountProjection.product.brand,
                    imageUrl = basketAndCountProjection.product.imageUrl,
                    store = basketAndCountProjection.product.store.name,
                    price = basketAndCountProjection.product.price,
                    category = basketAndCountProjection.product.category,
                    firstOption = basketAndCountProjection.product.firstOption,
                    secondOption = basketAndCountProjection.product.secondOption,
                    thirdOption = basketAndCountProjection.product.thirdOption,
                    waitingCount = basketAndCountProjection.waitingCount
                )
            }
        }
    }
}