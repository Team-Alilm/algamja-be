package org.team_alilm.application.port.out

import org.team_alilm.domain.Basket
import org.team_alilm.domain.FcmToken
import org.team_alilm.domain.Member
import org.team_alilm.domain.product.Product

interface LoadBasketAndMemberPort {

    fun loadBasketAndMember(product: Product): List<BasketAndMemberAndFcm>

    data class BasketAndMemberAndFcm(
        val basket: Basket,
        val member: Member,
        val fcmToken: FcmToken
    )
}