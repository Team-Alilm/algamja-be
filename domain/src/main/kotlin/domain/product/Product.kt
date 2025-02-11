package domain.product

import util.StringContextHolder

class Product (
    val id: ProductId? = null,
    val number: Long = 0,
    val name: String = "",
    val brand: String = "",
    val thumbnailUrl: String = "",
    val firstCategory: String = "",
    val secondCategory: String? = null,
    val price: Int = 0,
    val store: Store = Store.NONE,
    val firstOption: String? = null,
    val secondOption: String? = null,
    val thirdOption: String? = null
) {

    fun getManagedCode() : String? {
        return if (this.firstOption?.isNotBlank() == true && this.secondOption?.isNotBlank() == true && this.thirdOption?.isNotBlank() == true) {
            "${firstOption}^${secondOption}^${thirdOption}"
        } else if (firstOption?.isNotBlank() == true && secondOption?.isNotBlank() == true) {
            "${firstOption}^${secondOption}"
        } else {
            firstOption
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
                상품 구매링크 : ${getStoreUrl()}
                바구니에서 삭제되었습니다.
                "
        }
    """.trimIndent()

    fun getStoreUrl(): String {
        return when (store) {
            Store.MUSINSA -> StringContextHolder.MUSINSA_PRODUCT_URL.get().format(number)
            Store.CM29 -> StringContextHolder.CM29_PRODUCT_URL.get().format(number)
            Store.ZIGZAG -> StringContextHolder.ZIGZAG_PRODUCT_URL.get().format(number)
            else -> ""
        }
    }

    fun localServiceUrl() : String {
        return when (store) {
            Store.MUSINSA -> StringContextHolder.LOCAL_HOST.get().format(number)
            Store.CM29 -> StringContextHolder.LOCAL_HOST.get().format(number)
            Store.ZIGZAG -> StringContextHolder.LOCAL_HOST.get().format(number)
            else -> ""
        }
    }

    fun getZigzagOptionName () : String {
        return if (this.firstOption?.isNotBlank() == true && this.secondOption?.isNotBlank() == true && this.thirdOption?.isNotBlank() == true) {
            "${firstOption}/${secondOption}/${thirdOption}"
        } else if (firstOption?.isNotBlank() == true && secondOption?.isNotBlank() == true) {
            "${firstOption}/${secondOption}"
        } else {
            firstOption ?: ""
        }
    }
}