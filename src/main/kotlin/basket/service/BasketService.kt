package org.team_alilm.basket.service

import org.springframework.stereotype.Service
import org.team_alilm.basket.controller.dto.response.MyBasketProductListResponse
import org.team_alilm.basket.entity.Basket
import org.team_alilm.basket.repository.BasketQueryRepository
import org.team_alilm.basket.repository.BasketRepository

@Service
class BasketService(

    private val basketQueryRepository: BasketQueryRepository,
    private val basketRepository: BasketRepository,
) {

    fun getMyBasketProductList(memberId: Long) : MyBasketProductListResponse {
        val myBasketList = basketRepository.findByMemberId(memberId)
        return basketQueryRepository.getMyBasketProductList(myBasketList = myBasketList)
    }

    fun copyBasket(
        memberId: Long,
        productId: Long
    ) {
        val basket = Basket.of(memberId =  memberId, productId = productId)
        basketRepository.save(basket)
    }
}