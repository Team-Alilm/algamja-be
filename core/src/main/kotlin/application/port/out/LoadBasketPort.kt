package org.team_alilm.application.port.out

import org.team_alilm.domain.Basket
import org.team_alilm.domain.Member
import org.team_alilm.domain.Product

interface LoadBasketPort {

    fun loadBasketIncludeIsDelete(
        memberId: Member.MemberId,
        productId: Product.ProductId
    ): Basket?

    fun loadMyBasket(
        memberId: Member.MemberId
    ): List<Basket>

    fun loadBasketIncludeIsDelete(
        productId: Product.ProductId
    ): List<Basket>

    fun loadBasketIncludeIsDelete(
        productNumber: Number
    ): List<Basket>

    fun loadBasketIncludeIsDelete(
        memberId: Member.MemberId,
        productId: Product.ProductId,
        isDeleted: Boolean
    ): Basket?
}