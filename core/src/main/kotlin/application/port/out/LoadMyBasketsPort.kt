package org.team_alilm.application.port.out

import domain.Basket
import domain.Member
import domain.product.Product

interface LoadMyBasketsPort {

    fun loadMyBaskets(member: Member) : List<BasketAndProduct>

    data class BasketAndProduct(
        val basket: Basket,
        val product: Product,
    )

}