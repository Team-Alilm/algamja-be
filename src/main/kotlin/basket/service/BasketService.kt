package org.team_alilm.basket.service

import org.springframework.stereotype.Service
import org.team_alilm.basket.controller.dto.response.MyBasketItem
import org.team_alilm.basket.controller.dto.response.MyBasketItemListResponse
import org.team_alilm.basket.repository.BasketQueryRepository
import org.team_alilm.basket.repository.BasketRepository

@Service
class BasketService(

    private val basketRepository: BasketRepository,
    private val basketQueryRepository: BasketQueryRepository
) {

    fun getMyBasketItem(memberId: Long) : MyBasketItemListResponse =
        basketQueryRepository.getMyBasketItem(memberId)
}