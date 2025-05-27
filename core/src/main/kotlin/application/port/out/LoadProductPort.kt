package org.team_alilm.application.port.out

import domain.product.Product
import domain.product.Store

interface LoadProductPort {

    fun loadProduct(
        storeNumber: Long,
        store: Store,
        firstOption: String?,
        secondOption: String?,
        thirdOption: String?
    ): Product?

    fun loadProduct(
        productId: Long,
    ): Product?

    fun loadProductDetails(
        productId: Long,
    ): ProductAndWaitingCountAndImageList?

    fun loadRecentProduct(): List<Product>

    fun loadRelatedProduct(firstCategory: String, secondCategory: String?): List<Product>

    fun related(category: String) : List<Product>

    fun loadProductCategories(): List<String>

    data class ProductAndWaitingCountAndImageList(
        val product: Product,
        val waitingCount: Long,
        val imageUrlList: List<String>
    ) {
        companion object {
            fun of (product: Product, waitingCount: Long, imageUrlList: List<String>): ProductAndWaitingCountAndImageList {
                return ProductAndWaitingCountAndImageList(
                    product = product,
                    waitingCount = waitingCount,
                    imageUrlList = imageUrlList
                )
            }
        }
    }
}