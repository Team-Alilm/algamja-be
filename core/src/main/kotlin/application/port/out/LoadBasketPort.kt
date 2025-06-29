package org.team_alilm.application.port.out

import domain.Basket
import domain.Member
import org.team_alilm.adapter.out.persistence.adapter.data.ProductAndBasket

interface LoadBasketPort {

    fun loadBasketIncludeIsDelete(
        memberId: Member.MemberId,
        productId: Long
    ): Basket?

    fun loadBasketCount(
        productId: Long
    ): Long

    fun loadBasketList(
        productId: Long
    ): List<Basket>

    fun loadMyBasket(
        memberId: Member.MemberId
    ): List<Basket>

    fun loadBasketIncludeIsDelete(
        productId: Long
    ): List<Basket>

    fun loadBasketIncludeIsDelete(
        productNumber: Number
    ): List<Basket>

    fun loadOldBasket(
        memberId: Member.MemberId
    ): ProductAndBasket?

}