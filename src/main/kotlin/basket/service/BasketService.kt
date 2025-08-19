package org.team_alilm.basket.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.team_alilm.basket.controller.dto.response.MyBasketProduct
import org.team_alilm.basket.controller.dto.response.MyBasketProductListResponse
import org.team_alilm.basket.entity.BasketRow
import org.team_alilm.basket.repository.BasketExposedRepository
import org.team_alilm.common.exception.BusinessException
import org.team_alilm.common.exception.ErrorCode
import org.team_alilm.product.repository.ProductExposedRepository

@Service
@Transactional(readOnly = true)
class BasketService(
    private val basketExposedRepository: BasketExposedRepository,
    private val productExposedRepository: ProductExposedRepository
) {

    fun getMyBasketProductList(memberId: Long) : MyBasketProductListResponse {
        // 1) 회원의 장바구니 행 조회
        val basketRows = basketExposedRepository.fetchBasketsByMemberId(memberId)

        // 2) 상품 id만 추출(중복 제거)
        val productIds = basketRows.asSequence().map { it.productId }.distinct().toList()
        if (productIds.isEmpty()) {
            return MyBasketProductListResponse(myBasketProductList = emptyList())
        }

        // 3) 상품 정보 조회
        val productRows = productExposedRepository.fetchProductsByIds(productIds)

        val productWaitingCountProjection = basketExposedRepository.fetchWaitingCounts(productIds = productRows.map { it.id })

        // 4) 장바구니 상품 응답 생성
        val myBasketProductList = basketRows.map { basketRow ->
            val productRow = productRows.firstOrNull { it.id == basketRow.productId }
                ?: throw BusinessException(ErrorCode.PRODUCT_NOT_FOUND)

            val waitingCount = productWaitingCountProjection.firstOrNull { it.productId == productRow.id }?.waitingCount ?: 0L

            MyBasketProduct.from(
                productRow = productRow,
                waitingCount = waitingCount,
                basketRow = basketRow
            )
        }

        return MyBasketProductListResponse(myBasketProductList = myBasketProductList)
    }

    fun copyBasket(
        memberId: Long,
        productId: Long
    ) {
        if(basketExposedRepository.existsByMemberIdAndProductId(memberId, productId)) {
            return
        }


    }

    fun deleteBasket(memberId: Long, basketId: Long) {
        val basket = basketExposedRepository.findById(basketId)
            .orElseThrow { throw BusinessException(ErrorCode.BASKET_NOT_FOUND) }

        if (basket.memberId != memberId) {
            throw BusinessException(ErrorCode.MEMBER_NOT_FOUND_ERROR)
        }

        basket.delete()
        basketExposedRepository.save(basket)
    }
}