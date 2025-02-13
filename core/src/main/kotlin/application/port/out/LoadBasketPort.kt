package org.team_alilm.application.port.out

import domain.Basket
import domain.Member
import domain.product.ProductId
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Slice
import org.team_alilm.adapter.out.persistence.repository.product.ProductAndWaitingCount

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

    fun loadBasketSlice(
        pageRequest: PageRequest
    ): Slice<ProductAndWaitingCount>

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