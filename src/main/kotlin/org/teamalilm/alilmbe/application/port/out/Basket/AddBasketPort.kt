package org.teamalilm.alilmbe.application.port.out.Basket

import org.teamalilm.alilmbe.domain.basket.Basket
import org.teamalilm.alilmbe.domain.member.Member
import org.teamalilm.alilmbe.domain.product.Product

interface AddBasketPort {

    fun addBasket(
        basket: Basket,
        member: Member,
        product: Product
    ): Basket
}