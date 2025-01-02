package org.team_alilm.application.port.out

import domain.Basket
import domain.Member.*
import domain.product.ProductId

interface AddBasketPort {

    fun addBasket(
        basket: Basket,
        memberId: MemberId,
        productId: ProductId
    ): Basket

}