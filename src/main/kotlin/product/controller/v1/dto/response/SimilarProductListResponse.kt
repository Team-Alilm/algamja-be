package org.team_alilm.product.controller.v1.dto.response

import io.swagger.v3.oas.annotations.media.Schema

data class SimilarProductListResponse(

    @Schema(description = "유사 상품 리스트")
    val similarProductList: List<SimilarProductResponse>
)
