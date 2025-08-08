package org.team_alilm.product.controller.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "상품 상세 응답")
data class ProductDetailResponse(

    @field:Schema(description = "상품 ID", example = "101")
    val id: Long,

    @field:Schema(description = "외부몰 상품 번호", example = "202403150001")
    val number: Long,

    @field:Schema(description = "상품명", example = "오버핏 맨투맨")
    val name: String,

    @field:Schema(description = "브랜드명", example = "무신사 스탠다드")
    val brand: String,

    @field:Schema(description = "썸네일 이미지 URL", example = "https://cdn.example.com/thumb/101.jpg")
    val thumbnailUrl: String,

    @field:Schema(description = "상품 상세 이미지 URL 리스트", example = "[\"https://cdn.example.com/image/1.jpg\", \"https://cdn.example.com/image/2.jpg\"]")
    val imageUrlList: List<String>,

    @field:Schema(description = "스토어명", example = "MUSINSA")
    val store: String,

    @field:Schema(description = "판매가", example = "39900")
    val price: Long,

    @field:Schema(description = "첫 번째 옵션", example = "블랙")
    val firstOption: String?,

    @field:Schema(description = "두 번째 옵션", example = "L")
    val secondOption: String?,

    @field:Schema(description = "세 번째 옵션", example = "긴팔")
    val thirdOption: String?,

    @field:Schema(description = "현재 대기 인원 수", example = "25")
    val waitingCount: Long
)