package org.team_alilm.algamja.product.controller.v1.dto.response

import org.team_alilm.algamja.basket.entity.BasketRow
import org.team_alilm.algamja.product.entity.ProductRow

data class DelayedProductResponse(
    val productId: Long,
    val name: String,
    val brand: String,
    val thumbnailUrl: String,
    val store: String,
    val waitingDays: Long, // 대기 일수
    val addedDate: Long    // 장바구니 추가 날짜
) {
    companion object {
        fun from(productRow: ProductRow, basketRow: BasketRow): DelayedProductResponse {
            val currentTime = System.currentTimeMillis()
            val waitingDays = (currentTime - basketRow.createdAt) / (24 * 60 * 60 * 1000) // 밀리초를 일수로 변환
            
            return DelayedProductResponse(
                productId = productRow.id,
                name = productRow.name,
                brand = productRow.brand,
                thumbnailUrl = productRow.thumbnailUrl,
                store = productRow.store.name,
                waitingDays = waitingDays,
                addedDate = basketRow.createdAt
            )
        }
    }
}