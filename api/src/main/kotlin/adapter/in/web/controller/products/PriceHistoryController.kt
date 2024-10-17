package org.team_alilm.adapter.`in`.web.controller.products

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import org.springdoc.core.annotations.ParameterObject
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.team_alilm.application.port.`in`.use_case.PriceHistoryUseCase

@RestController
@RequestMapping("/api/v1/products")
@Tag(name = "상품 가격 히스토리 조회", description = "상품 가격 히스토리를 조회하는 API")
class PriceHistoryController(
    private val priceHistoryUseCase: PriceHistoryUseCase
) {

    @GetMapping("/price")
    @Operation(
        summary = "상품 가격 히스토리를 조회하는 API",
        description = """
            상품 Id를 받아서 상품의 가격 변동사항을 조회하는 API 입니다. 
            가격과 일시를 리스트로 응답해요.
        """
    )
    fun priceHistory(
        @ParameterObject
        @Valid
        request: PriceHistoryRequest,
    ) : ResponseEntity<PriceHistoryResponse> {
        return ResponseEntity.ok(
            PriceHistoryResponse.from(
                request.productId,
                priceHistoryUseCase.priceHistory(
                    PriceHistoryUseCase.PriceHistoryCommand(request.productId)
                )
            )
        )
    }

    @Schema(description = "상품 가격 히스토리 조회 요청")
    data class PriceHistoryRequest(
        @field:NotBlank(message = "상품 Id는 필수입니다.")
        val _productId: Long?
    ) {

        val productId: Long
            get() = _productId!!
    }

    @Schema(description = "상품 가격 히스토리 조회 응답")
    data class PriceHistoryResponse(
        val productId: Long,
        val priceHistoryList: List<PriceHistoryUseCase.PriceHistoryResult.PriceHistory>
    ) {

        companion object {
            fun from(productId: Long, result: PriceHistoryUseCase.PriceHistoryResult): PriceHistoryResponse {
                return PriceHistoryResponse(
                    productId = productId,
                    priceHistoryList = result.priceHistoryList
                )
            }
        }
    }
}
