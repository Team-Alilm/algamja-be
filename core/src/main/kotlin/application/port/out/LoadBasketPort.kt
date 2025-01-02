package org.team_alilm.application.port.out

import domain.Basket
import domain.Member
import domain.product.ProductId


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