package org.team_alilm.adapter.`in`.web.controller.baskets

import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import jakarta.validation.constraints.NotNull
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.team_alilm.application.port.`in`.use_case.DeleteBasketUseCase
import org.team_alilm.data.CustomMemberDetails

@RestController
@RequestMapping("/api/v1/baskets")
@Tag(name = "장바구니 삭제 API", description = "장바구니 삭제 관련 API")
class DeleteBasketController(
    private val deleteBasketUseCase: DeleteBasketUseCase
) {

    @DeleteMapping
    fun deleteBasket(
        @AuthenticationPrincipal customMemberDetails: CustomMemberDetails,
        @RequestBody @Valid request: DeleteBasketRequest
    ) : ResponseEntity<Unit> {
        val command = DeleteBasketUseCase.DeleteBasketCommand(
            memberId = customMemberDetails.member.id!!.value,
            basketId = request.basketId
        )

        deleteBasketUseCase.deleteBasket(command)

        return ResponseEntity.ok().build()
    }

    data class DeleteBasketRequest(
        @field:NotNull(message = "장바구니 ID는 필수입니다.")
        val basketId: Long
    )

}