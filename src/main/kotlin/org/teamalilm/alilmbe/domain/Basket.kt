package org.teamalilm.alilmbe.domain

import org.teamalilm.alilmbe.domain.Member.*
import org.teamalilm.alilmbe.domain.Product.*

class Basket(
    val id: BasketId?,
    val memberId: MemberId,
    val productId: ProductId
) {

    data class BasketId(val value: Long?)
}