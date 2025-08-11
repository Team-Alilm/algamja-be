package org.team_alilm.basket.controller.dto.response

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "내 장바구니 상품 목록 응답")
data class MyBasketItemListResponse(

    val items: List<MyBasketItem>
)