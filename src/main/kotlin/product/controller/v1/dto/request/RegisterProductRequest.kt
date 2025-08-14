package org.team_alilm.product.controller.v1.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern

@Schema(description = "상품 URL 등록 요청")
data class RegisterProductRequest(

    @field:NotBlank(message = "상품 URL은 필수입니다.")
    @field:Pattern(
        regexp = "^https?://.+",
        message = "상품 URL은 http 또는 https로 시작해야 합니다."
    )
    @Schema(
        description = "상품 상세 페이지 URL",
        example = "https://www.musinsa.com/app/goods/1234567",
        required = true
    )
    val productUrl: String
)