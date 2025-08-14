package org.team_alilm.product.controller.v1.dto.response

import io.swagger.v3.oas.annotations.media.Schema
import org.team_alilm.product.entity.Product
import org.team_alilm.product.repository.projection.ProductProjection

@Schema(description = "상품 응답 DTO")
data class ProductResponse(

    @Schema(description = "상품 ID", example = "101")
    val id: Long,

    @Schema(description = "상품명", example = "무신사 와이드 슬랙스")
    val name: String,

    @Schema(description = "브랜드명", example = "MuSinSa Standard")
    val brand: String,

    @Schema(description = "상품 썸네일 URL", example = "https://example.com/images/musinsa_slacks.jpg")
    val thumbnailUrl: String,

    @Schema(description = "스토어명", example = "MuSinSa")
    val store: String,

    @Schema(description = "가격(원)", example = "39000")
    val price: Long,

    @Schema(description = "1차 카테고리", example = "바지")
    val firstCategory: String,

    @Schema(description = "2차 카테고리", example = "슬랙스")
    val secondCategory: String?,

    @Schema(description = "옵션1", example = "블랙")
    val firstOption: String?,

    @Schema(description = "옵션2", example = "M")
    val secondOption: String?,

    @Schema(description = "옵션3", example = "롱 기장")
    val thirdOption: String?,

    @Schema(description = "대기 인원 수", example = "12")
    val waitingCount: Long
) {

    companion object {
        fun from(productProjection: ProductProjection, waitingCount: Long): ProductResponse {
            return ProductResponse(
                id = productProjection.id,
                name = productProjection.name,
                brand = productProjection.brand,
                thumbnailUrl = productProjection.thumbnailUrl,
                store = productProjection.store,
                price = productProjection.price.toLong(),
                firstCategory = productProjection.firstCategory,
                secondCategory = productProjection.secondCategory,
                firstOption = productProjection.firstOption,
                secondOption = productProjection.secondOption,
                thirdOption = productProjection.thirdOption,
                waitingCount = waitingCount
            )
        }
    }
}