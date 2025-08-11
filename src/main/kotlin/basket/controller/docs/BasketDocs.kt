package org.team_alilm.basket.controller.docs

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import org.team_alilm.basket.controller.dto.response.MyBasketItemListResponse
import org.team_alilm.common.security.CustomMemberDetails

interface BasketDocs {

    @Operation(
        summary = "내 장바구니 조회",
        description = "인증된 사용자의 장바구니를 조회합니다."
    )
    @ApiResponse(
        responseCode = "200",
        description = "정상 응답",
        content = [
            io.swagger.v3.oas.annotations.media.Content(
                mediaType = "application/json",
                schema = io.swagger.v3.oas.annotations.media.Schema(implementation = List::class),
                examples = [
                    io.swagger.v3.oas.annotations.media.ExampleObject(
                        name = "ok",
                        value = """
                        [
                          {
                            "productId": 1,
                            "productName": "상품명",
                            "quantity": 2,
                            "price": 10000
                          }
                        ]
                        """
                    )
                ]
            )
        ]
    )
    fun getMyBasketItem(customMemberDetails: CustomMemberDetails): common.response.ApiResponse<MyBasketItemListResponse>
}