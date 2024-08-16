package org.teamalilm.alilm.domain

import org.teamalilm.alilm.domain.Member.*
import org.teamalilm.alilm.domain.Product.*

class Basket(
    val id: BasketId?,
    val memberId: MemberId,
    val productId: ProductId,
    var isAlilm: Boolean = false,
    var alilmDate: Long?,
    val isHidden: Boolean,
    val createdDate: Long,
) {
    fun sendAlilm() {
        isAlilm = true
        alilmDate = System.currentTimeMillis()
    }

    data class BasketId(val value: Long?)

}