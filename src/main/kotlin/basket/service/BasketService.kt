package org.team_alilm.basket.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.team_alilm.basket.controller.dto.response.MyBasketProductListResponse
import org.team_alilm.basket.entity.Basket
import org.team_alilm.basket.repository.BasketQueryRepository
import org.team_alilm.basket.repository.BasketRepository
import org.team_alilm.common.exception.BusinessException
import org.team_alilm.common.exception.ErrorCode

@Service
@Transactional(readOnly = true)
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

    fun deleteBasket(memberId: Long, basketId: Long) {
        val basket = basketRepository.findById(basketId)
            .orElseThrow { throw BusinessException(ErrorCode.BASKET_NOT_FOUND) }

        if (basket.memberId != memberId) {
            throw BusinessException(ErrorCode.MEMBER_NOT_FOUND_ERROR)
        }

        basket.delete()
        basketRepository.save(basket)
    }
}