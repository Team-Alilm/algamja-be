package org.teamalilm.alilmbe.application.port.out.Basket

import org.teamalilm.alilmbe.adapter.out.persistence.entity.MemberJpaEntity
import org.teamalilm.alilmbe.domain.basket.Basket
import org.teamalilm.alilmbe.domain.product.Product

interface AddBasketPort {

    fun addBasket(
        basket: Basket,
        memberJpaEntity: MemberJpaEntity,
        product: Product
    ): Basket
}