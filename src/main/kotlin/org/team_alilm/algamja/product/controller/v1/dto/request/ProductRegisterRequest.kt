package org.team_alilm.algamja.product.controller.v1.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import org.team_alilm.algamja.common.enums.Store
import java.math.BigDecimal

@Schema(description = "상품 등록 요청 DTO")
data class ProductRegisterRequest(

    @Schema(description = "상품 번호", example = "7253154")
    @field:NotNull
    @field:Positive
    val number: Long,

    @Schema(description = "상품명", example = "나이키 에어포스 1")
    @field:NotBlank
    val name: String,

    @Schema(description = "브랜드명", example = "NIKE")
    @field:NotBlank
    val brand: String,

    @Schema(description = "상품 썸네일 URL", example = "https://image.msscdn.net/images/goods_img/202501/airforce1.png")
    @field:NotBlank
    val thumbnailUrl: String,

    @Schema(description = "상품 이미지 URL 목록", example = "[\"https://img.msscdn.net/1.png\", \"https://img.msscdn.net/2.png\"]")
    val imageUrlList: List<String>,

    @Schema(description = "스토어", example = "MUSINSA")
    @field:NotNull
    val store: Store,

    @Schema(description = "상품 가격", example = "129000")
    @field:NotNull
    @field:Positive
    val price: BigDecimal,

    @Schema(description = "1차 카테고리", example = "신발")
    @field:NotBlank
    val firstCategory: String,

    @Schema(description = "2차 카테고리", example = "운동화")
    val secondCategory: String?,

    @Schema(description = "1차 옵션", example = "블랙")
    @field:NotBlank
    val firstOption: String,

    @Schema(description = "2차 옵션", example = "M")
    val secondOption: String?,

    @Schema(description = "3차 옵션", example = "기본")
    val thirdOption: String?,
)