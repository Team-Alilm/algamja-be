package org.team_alilm.domain.product

import org.team_alilm.global.util.StringConstant

class Product (
    val id: ProductId?,
    val number: Long,
    val name: String,
    val brand: String,
    val thumbnailUrl: String,
    val firstCategory: String,
    val secondCategory: String?,
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
        require(thumbnailUrl.isNotBlank()) { "Product thumbnail URL must not be blank" }
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


    fun toSlackMessage(): String = """
        {
            "text":"${this.name} 상품이 재 입고 되었습니다.
        
                상품명: ${this.name}
                상품번호: ${this.number}
                상품 옵션1: ${this.firstOption}
                상품 옵션2: ${this.secondOption}
                상품 옵션3: ${this.thirdOption}
                상품 구매링크 : ${ StringConstant.MUSINSA_PRODUCT_HTML_REQUEST_URL.get().format(this.number)}
                바구니에서 삭제되었습니다.
                "
        }
    """.trimIndent()
}