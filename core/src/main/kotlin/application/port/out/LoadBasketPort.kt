package org.team_alilm.application.port.out

import domain.Basket
import domain.Member
import domain.product.ProductId
import org.springframework.data.domain.Slice

interface LoadBasketPort {

    fun loadBasketIncludeIsDelete(
        memberId: Member.MemberId,
        productId: ProductId
    ): Basket?

    fun loadBasketCount(
        productId: ProductId
    ): Long

    fun loadBasketList(
        productId: ProductId
    ): List<Basket>

    fun loadBasketPage(
        pageRequest: org.springframework.data.domain.PageRequest
    ): Slice<org.team_alilm.adapter.out.persistence.adapter.data.ProductAndWaitingCount>

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