package org.team_alilm.basket.controller.docs

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.team_alilm.basket.controller.dto.response.MyBasketProductListResponse
import org.team_alilm.common.security.CustomMemberDetails


@Tag(
    name = "Basket",
    description = "장바구니 관련 API"
)
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
    fun getMyBasketItem(
        @Parameter(hidden = true) customMemberDetails: CustomMemberDetails
    ): common.response.ApiResponse<MyBasketProductListResponse>

    @Operation(
        summary = "함께 기다리기",
        description = "사용자의 장바구니에 상품을 추가합니다."
    )
    @ApiResponse(
        responseCode = "200",
        description = "장바구니에 상품이 추가되었습니다.",
        content = [
            io.swagger.v3.oas.annotations.media.Content(
                mediaType = "application/json",
                schema = io.swagger.v3.oas.annotations.media.Schema(implementation = common.response.ApiResponse::class)
            )
        ]
    )
    fun copyBasket(
        @Parameter(hidden = true) customMemberDetails: CustomMemberDetails,
        productId: Long
    ): common.response.ApiResponse<Unit>

    @Operation(
        summary = "장바구니 삭제",
        description = "사용자의 장바구니에서 상품을 삭제합니다."
    )
    @ApiResponse(
        responseCode = "200",
        description = "장바구니에서 상품이 삭제되었습니다.",
        content = [
            io.swagger.v3.oas.annotations.media.Content(
                mediaType = "application/json",
                schema = io.swagger.v3.oas.annotations.media.Schema(implementation = common.response.ApiResponse::class)
            )
        ]
    )
    fun deleteBasket(@Parameter(hidden = true) customMemberDetails: CustomMemberDetails, basketId: Long): common.response.ApiResponse<Unit>
}