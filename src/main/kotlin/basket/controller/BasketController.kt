package org.team_alilm.basket.controller

import common.response.ApiResponse
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.team_alilm.basket.controller.docs.BasketDocs
import org.team_alilm.basket.controller.dto.response.MyBasketProductListResponse
import org.team_alilm.basket.service.BasketService
import org.team_alilm.common.exception.BusinessException
import org.team_alilm.common.exception.ErrorCode
import org.team_alilm.common.security.CustomMemberDetails

@RestController
@RequestMapping("/api/v1/baskets")
class BasketController(

    private val basketService: BasketService
) : BasketDocs {

    @GetMapping("/my")
    override fun getMyBasketItem(
        @AuthenticationPrincipal customMemberDetails: CustomMemberDetails,
    ): ApiResponse<MyBasketProductListResponse> {
        val response = basketService.getMyBasketProductList(
            memberId = customMemberDetails.member.id
                ?: throw BusinessException(ErrorCode.MEMBER_NOT_FOUND_ERROR)
        )

        return ApiResponse.success(response)
    }

    // 함께 담기 기능
    @PostMapping("/copy/{productId}")
    override fun copyBasket(
        @AuthenticationPrincipal customMemberDetails: CustomMemberDetails,
        @PathVariable productId: Long
    ): ApiResponse<Unit> {
        basketService.copyBasket(
            memberId = customMemberDetails.member.id
                ?: throw BusinessException(ErrorCode.MEMBER_NOT_FOUND_ERROR),
            productId = productId
        )
        return ApiResponse.success(Unit)
    }

    @DeleteMapping("/{basketId}")
    override fun deleteBasket(
        @AuthenticationPrincipal customMemberDetails: CustomMemberDetails,
        @PathVariable basketId: Long
    ): ApiResponse<Unit> {
        basketService.deleteBasket(
            memberId = customMemberDetails.member.id
                ?: throw BusinessException(ErrorCode.MEMBER_NOT_FOUND_ERROR),
            basketId = basketId
        )
        return ApiResponse.success(Unit)
    }
}