package org.team_alilm.application.port.out

import domain.Basket
import domain.FcmToken
import domain.Member
import domain.product.Product

interface LoadBasketAndMemberPort {

    fun loadBasketAndMember(product: Product): List<BasketAndMemberAndFcm>

    data class BasketAndMemberAndFcm(
        val basket: Basket,
        val member: Member,
        val fcmToken: FcmToken
    )
}