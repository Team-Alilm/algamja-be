package org.teamalilm.alilmbe.application.port.out.Basket

import org.teamalilm.alilmbe.domain.basket.Basket
import org.teamalilm.alilmbe.domain.member.Member
import org.teamalilm.alilmbe.domain.product.Product

interface LoadBasketPort {

    fun loadBasket(
        memberId: Member.MemberId,
        productId: Product.ProductId
    ): Basket?
}