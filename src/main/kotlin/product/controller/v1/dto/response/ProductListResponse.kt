package org.team_alilm.product.controller.v1.dto.response

import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "상품 목록 응답 DTO")
data class ProductListResponse(

    @ArraySchema(
        schema = Schema(implementation = ProductResponse::class),
        arraySchema = Schema(description = "상품 목록")
    )
    val productList: List<ProductResponse>,

    @Schema(description = "다음 페이지 존재 여부", example = "true")
    val hasNext: Boolean
)