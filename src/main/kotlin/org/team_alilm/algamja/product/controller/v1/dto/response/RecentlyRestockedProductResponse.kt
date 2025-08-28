package org.team_alilm.algamja.product.controller.v1.dto.response

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "최근 재 입고 상품 응답")
data class RecentlyRestockedProductResponse(
    @Schema(description = "상품 ID", example = "1")
    val productId: Long,

    @Schema(description = "상품명", example = "Recently Restocked Product 1")
    val name: String,

    @Schema(description = "브랜드명", example = "Brand C")
    val brand: String,

    @Schema(description = "상품 썸네일 URL", example = "http://example.com/restocked1.jpg")
    val thumbnailUrl: String
)