package org.teamalilm.alilmbe.application.port.out

import org.teamalilm.alilmbe.domain.Basket
import org.teamalilm.alilmbe.domain.Member
import org.teamalilm.alilmbe.domain.Product

interface AddBasketPort {

    fun addBasket(
        basket: Basket,
        member: Member,
        product: Product
    ): Basket

}