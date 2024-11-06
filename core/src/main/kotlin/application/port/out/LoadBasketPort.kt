package org.team_alilm.application.port.out

import org.team_alilm.domain.Basket
import org.team_alilm.domain.Member
import org.team_alilm.domain.product.Product
import org.team_alilm.domain.product.ProductId

interface LoadBasketPort {

    fun loadBasketIncludeIsDelete(
        memberId: Member.MemberId,
        productId: ProductId
    ): Basket?


    fun loadMyBasket(
        memberId: Member.MemberId
    ): List<Basket>

    fun loadBasketIncludeIsDelete(
        productId: ProductId
    ): List<Basket>

    fun loadBasketIncludeIsDelete(
        productNumber: Number
    ): List<Basket>

    fun loadBasketIncludeIsDelete(
        memberId: Member.MemberId,
        productId: ProductId,
        isDeleted: Boolean
    ): Basket?
}