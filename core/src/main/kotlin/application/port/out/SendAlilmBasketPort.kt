package org.team_alilm.application.port.out

import domain.Basket
import domain.Member
import domain.product.Product

interface SendAlilmBasketPort {

    fun addAlilmBasket(basket: Basket, member: Member, product: Product)
}