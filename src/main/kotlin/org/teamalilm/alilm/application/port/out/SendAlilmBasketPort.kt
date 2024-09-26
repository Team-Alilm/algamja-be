package org.teamalilm.alilm.application.port.out

import org.teamalilm.alilm.domain.Basket
import org.teamalilm.alilm.domain.Member
import org.teamalilm.alilm.domain.Product

interface SendAlilmBasketPort {

    fun addAlilmBasket(basket: Basket, member: Member, product: Product)
}