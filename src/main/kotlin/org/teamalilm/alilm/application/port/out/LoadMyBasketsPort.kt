package org.teamalilm.alilm.application.port.out

import org.teamalilm.alilm.domain.Basket
import org.teamalilm.alilm.domain.Member
import org.teamalilm.alilm.domain.Product

interface LoadMyBasketsPort {

    fun loadMyBaskets(member: Member) : List<BasketAndProduct>

    data class BasketAndProduct(
        val basket: Basket,
        val product: Product
    )

}