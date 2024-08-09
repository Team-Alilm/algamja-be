package org.teamalilm.alilm.application.port.out

import org.teamalilm.alilm.domain.Basket
import org.teamalilm.alilm.domain.Member
import org.teamalilm.alilm.domain.Product

interface LoadBasketPort {

    fun loadBasket(
        memberId: Member.MemberId,
        productId: Product.ProductId
    ): Basket?

}