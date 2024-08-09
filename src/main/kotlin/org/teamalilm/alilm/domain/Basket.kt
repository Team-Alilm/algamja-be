package org.teamalilm.alilm.domain

import org.teamalilm.alilm.domain.Member.*
import org.teamalilm.alilm.domain.Product.*

class Basket(
    val id: BasketId?,
    val memberId: MemberId,
    val productId: ProductId,
    val isHidden: Boolean,
    val createdDate: Long,
) {

    data class BasketId(val value: Long?)

}