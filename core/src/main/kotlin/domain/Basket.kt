package org.team_alilm.domain

import org.team_alilm.domain.Member.*
import org.team_alilm.domain.Product.*

class Basket(
    val id: BasketId? = null,
    val memberId: MemberId,
    val productId: ProductId,
    var isAlilm: Boolean = false,
    var alilmDate: Long? = null,
    val isHidden: Boolean = false,
) {
    fun sendAlilm() {
        isAlilm = true
        alilmDate = System.currentTimeMillis()
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