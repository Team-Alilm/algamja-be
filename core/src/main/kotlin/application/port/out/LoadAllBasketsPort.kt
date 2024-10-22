package org.team_alilm.application.port.out

import org.team_alilm.domain.Basket
import org.team_alilm.domain.Member
import org.team_alilm.domain.Product

interface LoadAllBasketsPort {

    fun loadAllBaskets() : List<BasketAndMemberAndProduct>

    data class BasketAndMemberAndProduct(
        val basket: Basket,
        val member: Member,
        val product: Product,
    )

}