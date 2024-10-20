package org.team_alilm.application.port.out

import org.team_alilm.domain.Basket
import org.team_alilm.domain.Member
import org.team_alilm.domain.Product

interface LoadMyBasketsPort {

    fun loadMyBaskets(member: Member) : List<BasketAndProduct>

    data class BasketAndProduct(
        val basket: Basket,
        val product: Product,
        val waitingCount: Long
    )

}