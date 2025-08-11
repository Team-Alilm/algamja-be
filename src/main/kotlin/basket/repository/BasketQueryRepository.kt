package org.team_alilm.basket.repository

import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.stereotype.Repository
import org.team_alilm.basket.controller.dto.response.MyBasketItemListResponse
import org.team_alilm.basket.exposed.BasketTable

@Repository
class BasketQueryRepository {

    fun getMyBasketItem(memberId: Long): MyBasketItemListResponse = transaction {
        val rows = BasketTable
    }


}