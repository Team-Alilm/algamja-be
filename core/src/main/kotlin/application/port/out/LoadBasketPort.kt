package org.team_alilm.application.port.out

import org.team_alilm.domain.Basket
import org.team_alilm.domain.Member
import org.team_alilm.domain.Product

interface LoadBasketPort {

    fun loadBasket(
        memberId: Member.MemberId,
        productId: Product.ProductId
    ): Basket?

    fun loadBasket(
        memberId: Member.MemberId
    ): List<Basket>

}