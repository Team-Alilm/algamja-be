package org.team_alilm.application.port.out

import org.team_alilm.domain.product.Product
import org.team_alilm.domain.product.ProductId
import org.team_alilm.domain.product.Store

interface LoadProductPort {

    fun loadProduct(
        number:Long,
        store: Store,
        firstOption: String,
        secondOption: String?,
        thirdOption: String?
    ): Product?

    fun loadProduct(
        productId: ProductId,
    ): Product?

    fun loadProductDetails(
        productId: ProductId,
    ): ProductAndWaitingCountAndImageList?

    fun loadRecentProduct(): List<Product>

    fun loadRelatedProduct(category: String): List<Product>

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