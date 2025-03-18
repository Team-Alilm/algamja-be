package org.team_alilm.controller.baskets

import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.team_alilm.application.port.`in`.use_case.OldBasketUseCase
import org.team_alilm.data.CustomMemberDetails

@RestController
@RequestMapping("/api/v1/baskets")
@Tag(name = "OldBasket", description = "오랫 동안 기다린 장바구니 조회 API")
class OldBasketController(
    private val oldBasketUseCase: OldBasketUseCase,
) {

    @Schema(description = "오랫동안 기다린 장바구니 조회 API")
    @GetMapping("/old")
    fun getOldBasket(
        @AuthenticationPrincipal customMemberDetails: CustomMemberDetails
    ): ResponseEntity<OldBasketResponse> {

        val command = OldBasketUseCase.OldBasketCommand(
            memberId = customMemberDetails.member.id!!
        )

        val result = oldBasketUseCase.loadOldBasket(command)

        val response = OldBasketResponse(
            oldProduct = OldProduct(
                createdDate = result.oldProductInfo.createdDate,
                thumbnailUrl = result.oldProductInfo.thumbnailUrl
            ),
            relatedProductList = result.relatedProductList.map {
                RelateProduct(
                    thumbnailUrl = it.thumbnailUrl
                )
            }

        )

        return ResponseEntity.ok(response)
    }

    data class OldBasketResponse(
        val oldProduct: OldProduct,
        val relatedProductList: List<RelateProduct>
    )

    data class OldProduct(
        val thumbnailUrl: String,
        val createdDate: Long
    )

    data class RelateProduct(
        val thumbnailUrl: String,
    )

}