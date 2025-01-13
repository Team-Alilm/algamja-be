package org.team_alilm.adapter.`in`.web.controller.baskets

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.team_alilm.application.port.`in`.use_case.BasketAlilmUseCase

@RestController
@RequestMapping("/api/v1/baskets")
@Tag(name = "장바구니 API", description = "장바구니 API")
class BasketAlilmController(
    private val basketAlilmUseCase: BasketAlilmUseCase
) {

    @PutMapping("/alilm")
    fun basketAlilm(
        @RequestBody request: BasketAlilmRequest
    ): ResponseEntity<Unit> {
        basketAlilmUseCase.basketAlilm(
            BasketAlilmUseCase.BasketAlilmCommand(
                productId = request.productId
            )
        )

        return ResponseEntity.ok().build()
    }

    data class BasketAlilmRequest(
        @JsonProperty("productId") val productId: Long,
    )
}