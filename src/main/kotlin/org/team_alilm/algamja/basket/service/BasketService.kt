package org.team_alilm.algamja.basket.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.team_alilm.algamja.basket.controller.dto.response.MyBasketProduct
import org.team_alilm.algamja.basket.controller.dto.response.MyBasketProductListResponse
import org.team_alilm.algamja.basket.repository.BasketExposedRepository
import org.team_alilm.algamja.common.exception.BusinessException
import org.team_alilm.algamja.common.exception.ErrorCode
import org.team_alilm.algamja.product.entity.ProductRow
import org.team_alilm.algamja.product.repository.ProductExposedRepository
import org.team_alilm.algamja.product.repository.projection.ProductWaitingCountProjection

@Service
@Transactional(readOnly = true)
class BasketService(
    private val basketExposedRepository: BasketExposedRepository,
    private val productExposedRepository: ProductExposedRepository
) {

    fun getMyBasketProductList(memberId: Long): MyBasketProductListResponse {
        // 내 장바구니 행 조회
        val basketRows = basketExposedRepository.fetchBasketsByMemberId(memberId)
        if (basketRows.isEmpty()) {
            return MyBasketProductListResponse(emptyList())
        }

        // 상품 ID 목록 추출(중복 제거)
        val productIds = basketRows.asSequence().map { it.productId }.distinct().toList()

        // 상품 / 대기 인원수 조회 (각 1회)
        val productRows: List<ProductRow> =
            productExposedRepository.fetchProductsByIds(productIds)

        val waitingCountProjections: List<ProductWaitingCountProjection> =
            basketExposedRepository.fetchWaitingCounts(productIds)

        // 빠른 조회를 위한 인덱스(Map) 구성
        val productById: Map<Long, ProductRow> = indexProductsById(productRows)
        val waitingCountByProductId: Map<Long, Long> = indexWaitingCountsByProductId(waitingCountProjections)

        // 응답 매핑 (누락된 상품은 스킵)
        val myBasketProducts = mutableListOf<MyBasketProduct>()
        for (basketRow in basketRows) {
            val productRow = productById[basketRow.productId] ?: continue
            val waitingCount = waitingCountByProductId[productRow.id] ?: 0L

            myBasketProducts += MyBasketProduct.from(
                productRow = productRow,
                waitingCount = waitingCount,
                basketRow = basketRow
            )
        }

        return MyBasketProductListResponse(myBasketProducts)
    }

    @Transactional
    fun copyBasket(memberId: Long, productId: Long) {
        // 삭제/숨김 포함해서 먼저 조회 (복원/재노출 고려)
        val found = basketExposedRepository.fetchAnyBasketByMemberIdAndProductId(memberId, productId)

        when {
            found == null -> {
                // 아무 것도 없으면 새로 생성
                basketExposedRepository.createBasket(memberId = memberId, productId = productId)
            }
            found.isDelete || found.isHidden -> {
                // 삭제된 상태면 복원
                basketExposedRepository.restoreBasket(basketId = found.id)
            }
            else -> {
                // 이미 활성 상태면 아무 것도 안 함 (idempotent)
            }
        }
    }

    @Transactional
    fun deleteBasket(
        memberId: Long,
        basketId: Long
    ) {
        // 장바구니 행 조회
        val basketRow = basketExposedRepository.fetchBasketById(basketId)
        // 존재하지 않거나, 해당 회원의 장바구니가 아니면 예외
        if (basketRow == null || basketRow.memberId != memberId) {
            throw BusinessException(ErrorCode.BASKET_NOT_FOUND)
        }

        // 삭제 처리
        basketExposedRepository.deleteBasket(
            basketId = basketId
        )
    }

    // ---------- private helpers ----------
    private fun indexProductsById(productRows: List<ProductRow>): Map<Long, ProductRow> {
        // 상품 ID로 빠르게 조회할 수 있도록 Map으로 인덱싱
        val map = HashMap<Long, ProductRow>(productRows.size.coerceAtLeast(16))

        for (productRow in productRows) {
            map[productRow.id] = productRow
        }
        return map
    }

    private fun indexWaitingCountsByProductId(
        projections: List<ProductWaitingCountProjection>
    ): Map<Long, Long> {
        val map = HashMap<Long, Long>(projections.size.coerceAtLeast(16))

        for (projection in projections) {
            map[projection.productId] = projection.waitingCount
        }

        return map
    }
}