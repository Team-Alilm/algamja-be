package org.teamalilm.alilmbe.application.port.out

import org.teamalilm.alilmbe.domain.Basket
import org.teamalilm.alilmbe.domain.Member
import org.teamalilm.alilmbe.domain.Product

interface LoadBasketPort {

    fun loadBasket(
        memberId: Member.MemberId,
        productId: Product.ProductId
    ): Basket?
}