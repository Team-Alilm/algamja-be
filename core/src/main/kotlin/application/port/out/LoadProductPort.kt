package org.team_alilm.application.port.out

import domain.product.Product
import domain.product.ProductId
import domain.product.Store
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Slice
import org.team_alilm.adapter.out.persistence.repository.product.ProductAndWaitingCount

interface LoadProductPort {

    fun loadProductSlice(
        pageRequest: PageRequest
    ): Slice<ProductAndWaitingCount>

    fun loadProduct(
        number:Long,
        store: Store,
        firstOption: String?,
        secondOption: String?,
        thirdOption: String?
    ): Product?

    fun loadProduct(
        productId: ProductId,
    ): Product?

    fun loadProduct(
        productId: Long,
    ): Product?

    fun loadProductDetails(
        productId: ProductId,
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