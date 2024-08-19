package org.teamalilm.alilm.adapter.`in`.web.controller.baskets

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.teamalilm.alilm.adapter.out.security.CustomMemberDetails
import org.teamalilm.alilm.application.port.`in`.use_case.CopyBasketUseCase
import org.teamalilm.alilm.common.error.RequestValidateException

@RestController
@RequestMapping("/api/v1/baskets")
class CopyBasketController(
    private val copyBasketUseCase: CopyBasketUseCase
) {

    @PostMapping("/copy")
    fun copyBasket(
        @RequestBody request: CopyBasketRequest,
        @AuthenticationPrincipal customMemberDetails: CustomMemberDetails,
        bindingResult: BindingResult
    ) : ResponseEntity<Unit> {
        if (bindingResult.hasErrors()) {
            throw RequestValidateException(bindingResult)
        }

        val command = CopyBasketUseCase.CopyBasketCommand(
            productId = request.productId,
            customMemberDetails = customMemberDetails
        )

        copyBasketUseCase.copyBasket(command)

        return ResponseEntity.ok().build()
    }

    @Schema(description = "장바구니 복사 요청")
    data class CopyBasketRequest(
        @field:NotNull(message = "상품 ID는 필수입니다.")
        @field:Positive(message = "상품 ID는 양수여야 합니다.")
        @field:Schema(
            example = "1",
            description = "상품 ID",
            format = "int64",
            required = true,
            type = "integer"
        )
        val productId: Long
    )
}