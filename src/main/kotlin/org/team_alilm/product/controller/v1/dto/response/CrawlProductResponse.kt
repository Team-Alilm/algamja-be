package org.team_alilm.product.controller.v1.dto.response

import io.swagger.v3.oas.annotations.media.Schema
import java.math.BigDecimal

@Schema(description = "크롤링된 상품 응답 DTO")
data class CrawlProductResponse(

    @Schema(description = "상품 번호", example = "7253154")
    val number: Long,

    @Schema(description = "상품명", example = "나이키 에어포스 1")
    val name: String,

    @Schema(description = "브랜드명", example = "NIKE")
    val brand: String,

    @Schema(description = "상품 썸네일 URL", example = "https://image.msscdn.net/images/goods_img/202501/airforce1.png")
    val thumbnailUrl: String,

    @Schema(description = "상품 이미지 URL 목록", example = "[\"https://img.msscdn.net/1.png\", \"https://img.msscdn.net/2.png\"]")
    val imageUrlList: List<String>,

    @Schema(description = "스토어명", example = "무신사 스토어")
    val store: String,

    @Schema(description = "상품 가격", example = "129000")
    val price: BigDecimal,

    @Schema(description = "1차 카테고리", example = "신발")
    val firstCategory: String,

    @Schema(description = "2차 카테고리", example = "운동화")
    val secondCategory: String?,

    @Schema(description = "1차 옵션 목록", example = "[\"블랙\", \"화이트\"]")
    val firstOptions: List<String>,

    @Schema(description = "2차 옵션 목록", example = "[\"S\", \"M\", \"L\"]")
    val secondOptions: List<String>,

    @Schema(description = "3차 옵션 목록", example = "[\"기본\", \"한정판\"]")
    val thirdOptions: List<String>,
)