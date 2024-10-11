package org.team_alilm.application.port.out

import org.team_alilm.domain.Basket
import org.team_alilm.domain.Member
import org.team_alilm.domain.Product

interface AddBasketPort {

    fun addBasket(
        basket: Basket,
        member: Member,
        product: Product
    ): Basket

}