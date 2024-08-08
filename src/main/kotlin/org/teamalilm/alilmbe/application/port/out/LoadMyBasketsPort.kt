package org.teamalilm.alilmbe.application.port.out

import org.teamalilm.alilmbe.domain.Basket
import org.teamalilm.alilmbe.domain.Member
import org.teamalilm.alilmbe.domain.Product

interface LoadMyBasketsPort {

    fun loadMyBaskets(member: Member) : List<BasketAndProduct>

    data class BasketAndProduct(
        val basket: Basket,
        val product: Product
    )

}