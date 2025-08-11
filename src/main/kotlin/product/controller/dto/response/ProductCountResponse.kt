package org.team_alilm.product.controller.dto.response

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "상품 개수 응답")
data class ProductCountResponse(

    @field:Schema(
        description = "소프트 삭제되지 않은 상품의 총 개수",
        example = "1234"
    )
    val productCount: Long
)