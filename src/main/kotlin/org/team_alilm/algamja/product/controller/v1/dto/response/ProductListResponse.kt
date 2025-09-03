package org.team_alilm.algamja.product.controller.v1.dto.response

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
    val hasNext: Boolean,

    @Schema(description = "마지막 상품 ID (무한 스크롤용)", example = "100")
    val lastProductId: Long? = null,

    @Schema(description = "마지막 상품 가격 (가격 정렬 시 사용)", example = "50000")
    val lastPrice: Int? = null,

    @Schema(description = "마지막 상품 대기자수 (대기자수 정렬 시 사용)", example = "10")
    val lastWaitingCount: Long? = null
)