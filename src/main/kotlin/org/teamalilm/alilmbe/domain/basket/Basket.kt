package org.teamalilm.alilmbe.domain.basket

import org.teamalilm.alilmbe.domain.member.Member.*
import org.teamalilm.alilmbe.domain.product.Product.*

class Basket(
    val id: BasketId,
    val memberId: MemberId,
    val productId: ProductId
) {

    data class BasketId(val value: Long?)
}