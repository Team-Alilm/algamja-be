package org.teamalilm.alilmbe.application.port.out

import org.teamalilm.alilmbe.domain.Basket

interface UpdateBasketPort {

    fun deleteBasket(basketId: Basket.BasketId)
}