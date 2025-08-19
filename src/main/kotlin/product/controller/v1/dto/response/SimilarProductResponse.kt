package org.team_alilm.product.controller.v1.dto.response

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "유사 상품 응답")
data class SimilarProductResponse(

    @Schema(description = "상품 ID", example = "101")
    val productId: Long,

    @Schema(description = "상품명", example = "삼성 갤럭시 S24 울트라")
    val name: String,

    @Schema(description = "브랜드명", example = "삼성전자")
    val brand: String,

    @Schema(description = "상품 썸네일 URL", example = "https://example.com/images/galaxy_s24.jpg")
    val thumbnailUrl: String
) {

    companion object {
        fun from(product: Product): SimilarProductResponse {
            return SimilarProductResponse(
                productId = product.id!!,
                name = product.name,
                brand = product.brand,
                thumbnailUrl = product.thumbnailUrl
            )
        }
    }
}