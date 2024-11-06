package org.team_alilm.domain.product

class Product (
    val id: ProductId?,
    val number: Long,
    val name: String,
    val brand: String,
    val imageUrl: String,
    val category: String,
    val price: Int,
    val store: Store,
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
    }



    fun getManagedCode() : String {
        return if (this.firstOption.isNotBlank() && this.secondOption?.isNotBlank() == true && this.thirdOption?.isNotBlank() == true) {
            "${firstOption}^${secondOption}^${thirdOption}"
        } else if (firstOption.isNotBlank() && secondOption?.isNotBlank() == true) {
            "${firstOption}^${secondOption}"
        } else {
            firstOption
        }
    }

    fun getEmailOption() : String {
        return if (this.firstOption.isNotBlank() && this.secondOption?.isNotBlank() == true && this.thirdOption?.isNotBlank() == true) {
            "${this.firstOption} / ${this.secondOption} / ${this.thirdOption}"
        } else if (this.firstOption.isNotBlank() && this.secondOption?.isNotBlank() == true) {
            "${this.firstOption} / ${this.secondOption}"
        } else {
            this.firstOption
        }
    }
}