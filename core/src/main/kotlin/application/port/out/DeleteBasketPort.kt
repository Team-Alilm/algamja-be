package org.team_alilm.application.port.out

interface DeleteBasketPort {

        fun deleteBasket(
            memberId: Long,
            basketId: Long
        )
}