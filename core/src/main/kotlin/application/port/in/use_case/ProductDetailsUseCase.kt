package org.team_alilm.application.port.`in`.use_case

import org.team_alilm.domain.product.Product

interface ProductDetailsUseCase {

    fun productDetails(command: ProductDetailsCommand): ProductDetailsResult

    data class ProductDetailsCommand(
        val productId: Long
    )

    data class ProductDetailsResult(
        val id: Long,
        val number: Long,
        val name: String,
        val brand: String,
        val thumbnailUrl: String,
        val imageUrlList: List<String>,
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
            fun from (product: Product, waitingCount: Long, imageUrlList: List<String>): ProductDetailsResult {
                return ProductDetailsResult(
                    id = product.id!!.value,
                    number = product.number,
                    name = product.name,
                    brand = product.brand,
                    thumbnailUrl = product.thumbnailUrl,
                    imageUrlList = imageUrlList,
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