package org.team_alilm.algamja.product.controller.v1.dto.param

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Size

@Schema(description = "상품 개수 조회 요청 파라미터")
data class ProductCountParam(

    @field:Size(max = 100, message = "검색 키워드는 최대 100자까지 입력 가능합니다.")
    @Schema(description = "검색 키워드", example = "노트북", maxLength = 100)
    val keyword: String? = null,

    @field:Size(max = 50, message = "카테고리 코드는 최대 50자까지 입력 가능합니다.")
    @Schema(description = "카테고리 코드", example = "ELECTRONICS", maxLength = 50)
    val category: String? = null
)