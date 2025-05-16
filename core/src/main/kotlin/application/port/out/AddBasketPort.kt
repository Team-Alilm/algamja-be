package org.team_alilm.application.port.out

import domain.Basket
import domain.Member.*

interface AddBasketPort {

    fun addBasket(
        basket: Basket,
        memberId: MemberId,
        productId: Long
    ): Basket

}