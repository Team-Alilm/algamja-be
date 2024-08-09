package org.teamalilm.alilm.application.port.out

import org.teamalilm.alilm.domain.Basket

interface UpdateBasketPort {

    fun deleteBasket(basketId: Basket.BasketId)
}