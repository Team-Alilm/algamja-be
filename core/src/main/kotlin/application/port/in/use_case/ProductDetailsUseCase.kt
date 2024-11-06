package org.team_alilm.application.port.`in`.use_case

import org.team_alilm.domain.product.Product

interface ProductDetailsUseCase {

    fun productDetails(command: ProductDetailsCommand): ProductDetailsResponse

    data class ProductDetailsCommand(
        val productId: Long
    )

    data class ProductDetailsResponse(
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
            fun from (product: Product, waitingCount: Long): ProductDetailsResponse {
                return ProductDetailsResponse(
                    id = product.id!!.value,
                    number = product.number,
                    name = product.name,
                    brand = product.brand,
                    imageUrl = product.imageUrl,
                    store = product.store.name,
                    price = product.price,
                    category = product.category,
                    firstOption = product.firstOption,
                    secondOption = product.secondOption,
                    thirdOption = product.thirdOption,
                    waitingCount = waitingCount
                )
            }
        }
    }
}