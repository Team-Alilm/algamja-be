package org.teamalilm.alilmbe.application.port.out

import org.teamalilm.alilmbe.domain.Basket
import org.teamalilm.alilmbe.domain.Member
import org.teamalilm.alilmbe.domain.Product

interface LoadAllBasketsPort {

    fun loadAllBaskets() : List<BasketAndMemberAndProduct>

    data class BasketAndMemberAndProduct(
        val basket: Basket,
        val member: Member,
        val product: Product
    )
}