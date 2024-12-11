package org.team_alilm.application.port.out

import org.team_alilm.domain.Basket
import org.team_alilm.domain.Member
import org.team_alilm.domain.product.Product

interface LoadBasketAndMemberPort {

    fun loadBasketAndMember(product: Product): List<BasketAndMember>

    data class BasketAndMember(
        val basket: Basket,
        val member: Member
    )
}