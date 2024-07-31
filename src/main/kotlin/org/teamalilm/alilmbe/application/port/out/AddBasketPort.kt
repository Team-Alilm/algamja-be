package org.teamalilm.alilmbe.application.port.out

import org.teamalilm.alilmbe.adapter.out.persistence.entity.MemberJpaEntity
import org.teamalilm.alilmbe.domain.Basket
import org.teamalilm.alilmbe.domain.Product

interface AddBasketPort {

    fun addBasket(
        basket: Basket,
        memberJpaEntity: MemberJpaEntity,
        product: Product
    ): Basket
}