package domain

import domain.Member.*
import domain.product.ProductId

class Basket(
    val id: BasketId? = null,
    val memberId: MemberId,
    val productId: ProductId,
    var isAlilm: Boolean = false,
    var alilmDate: Long? = null,
    var isHidden: Boolean = false,
    var isDelete: Boolean = false
) {

    fun sendAlilm() {
        isAlilm = true
        alilmDate = System.currentTimeMillis()
    }

    // 장바구니를 보고 재 등록 여부를 확인 합니다.
    fun isReRegisterable (): Boolean {
        if (isAlilm || isDelete) {
            isAlilm = false
            alilmDate = null
            isDelete = false
            return true
        } else {
            return false
        }
    }

    data class BasketId(val value: Long?)

    companion object {
        fun create(memberId: MemberId, productId: ProductId): Basket {
            return Basket(
                memberId = memberId,
                productId = productId
            )
        }
    }
}