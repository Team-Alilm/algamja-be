package domain

import domain.Member.*

class Basket(
    val id: BasketId? = null,
    val memberId: MemberId,
    val productId: Long,
    var isAlilm: Boolean = false,
    var alilmDate: Long? = null,
    var isHidden: Boolean = false,
    var isDelete: Boolean = false,
    val createdDate: Long = System.currentTimeMillis()
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
        fun create(memberId: MemberId, productId: Long): Basket {
            return Basket(
                memberId = memberId,
                productId = productId
            )
        }
    }
}