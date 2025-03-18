package org.team_alilm.controller.baskets

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.team_alilm.application.port.`in`.use_case.CopyBasketUseCase
import org.team_alilm.data.CustomMemberDetails

@RestController
@RequestMapping("/api/v1/baskets")
@Tag(name = "장바구니 복사 API", description = "같이 기다리기 기능을 제공하는 API")
class CopyBasketController(
    private val copyBasketUseCase: CopyBasketUseCase
) {

    @Operation(
        summary = "함께 기다리기 기능 API",
        description = """
            사용자의 장바구니에 상품을 추가하는 API 입니다.
        """
    )
    @PostMapping("/copy")
    fun copyBasket(
        @RequestBody request: CopyBasketRequest,
        @AuthenticationPrincipal customMemberDetails: CustomMemberDetails,
    ) : ResponseEntity<Unit> {
        val command = CopyBasketUseCase.CopyBasketCommand(
            productId = request.productId,
            member = customMemberDetails.member
        )

        copyBasketUseCase.copyBasket(command)

        return ResponseEntity.ok().build()
    }

    @Schema(description = "장바구니 복사 요청")
    data class CopyBasketRequest(
        val productId: Long
    )
}