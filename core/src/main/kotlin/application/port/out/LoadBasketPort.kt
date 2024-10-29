package org.team_alilm.application.port.out

import org.team_alilm.domain.Basket
import org.team_alilm.domain.Member
import org.team_alilm.domain.Product

interface LoadBasketPort {

    fun loadBasket(
        memberId: Member.MemberId,
        productId: Product.ProductId
    ): Basket?

    fun loadMyBasket(
        memberId: Member.MemberId
    ): List<Basket>

    fun loadBasket(
        productId: Product.ProductId
    ): List<Basket>

    fun loadBasket(
        productNumber: Number
    ): List<Basket>
}