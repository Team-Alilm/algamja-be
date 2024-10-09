package org.team_alilm.domain

class Product (
    val id: ProductId?,
    val number: Long,
    val name: String,
    val brand: String,
    val imageUrl: String,
    val category: String,
    val price: Int,
    val store: Store,
    val waitingCount: Int = 0,
    val firstOption: String,
    val secondOption: String?,
    val thirdOption: String?
) {
    init {
        require(number > 0) { "Product number must be positive" }
        require(name.isNotBlank()) { "Product name must not be blank" }
        require(brand.isNotBlank()) { "Product brand must not be blank" }
        require(imageUrl.isNotBlank()) { "Product image URL must not be blank" }
        require(category.isNotBlank()) { "Product category must not be blank" }
        require(price >= 0) { "Product price must be non-negative" }
        require(firstOption.isNotBlank()) { "Product option1 must not be blank" }
        require(waitingCount >= 0) { "Product waiting count must be non-negative" }
    }

    data class ProductId(val value: Long)

    enum class Store {

        MUSINSA;

    }
}