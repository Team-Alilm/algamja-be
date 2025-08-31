package org.team_alilm.algamja.product.controller.v1.dto.response

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "상품 등록 응답 DTO")
data class ProductRegisterResponse(

    @Schema(description = "등록된 상품 ID", example = "123")
    val productId: Long,

    @Schema(description = "상품명", example = "나이키 에어포스 1")
    val productName: String,

    @Schema(description = "등록 성공 여부", example = "true")
    val success: Boolean = true
)