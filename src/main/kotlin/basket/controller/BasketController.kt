package org.team_alilm.basket.controller

import common.response.ApiResponse
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.team_alilm.basket.controller.docs.BasketDocs
import org.team_alilm.basket.controller.dto.response.MyBasketItemListResponse
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
    ): ApiResponse<MyBasketItemListResponse> {
        val response = basketService.getMyBasketItem(
            memberId = customMemberDetails.member.id
                ?: throw BusinessException(ErrorCode.MEMBER_NOT_FOUND_ERROR)
        )

        return ApiResponse.success(response)
    }
}