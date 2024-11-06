package org.team_alilm.application.port.out

import org.team_alilm.domain.Basket
import org.team_alilm.domain.Member
import org.team_alilm.domain.Member.*
import org.team_alilm.domain.product.Product
import org.team_alilm.domain.product.ProductId

interface AddBasketPort {

    fun addBasket(
        basket: Basket,
        memberId: MemberId,
        productId: ProductId
    ): Basket

}