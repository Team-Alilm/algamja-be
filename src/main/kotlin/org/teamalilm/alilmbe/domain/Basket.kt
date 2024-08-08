package org.teamalilm.alilmbe.domain

import org.teamalilm.alilmbe.domain.Member.*
import org.teamalilm.alilmbe.domain.Product.*

class Basket(
    val id: BasketId?,
    val memberId: MemberId,
    val productId: ProductId,
    val isHidden: Boolean,
    val createdDate: Long,
) {

    data class BasketId(val value: Long?)

}