package org.teamalilm.alilmbe.adapter.`in`.web.controller.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import org.teamalilm.alilmbe.adapter.out.persistence.entity.product.Store

@Schema(description = "Alilm 등록 request")
data class AlilmRegistrationRequest(
    @NotNull(message = "상품 번호는 필수에요.")
    @Schema(description = "상품 번호", example = "123456")
    val number: Int,

    @NotBlank(message = "상품 명은 필수에요.")
    @Schema(description = "이름", example = "COOL 롱 슬리브 셔츠 STYLE 3 TIPE")
    val name: String,

    @NotBlank(message = "브랜드는 필수에요.")
    @Schema(description = "브랜드", example = "Alilm Brand")
    val brand: String,

    @NotBlank(message = "이미지 링크는 필수에요.")
    @Schema(
        description = "main 이미지 링크",
        example = "https://image.msscdn.net/images/goods_img/20240208/3859221/3859221_17084100068855_500.jpg"
    )
    val imageUrl: String,

    @NotBlank(message = "상품 카테고리는 필수에요.")
    @Schema(description = "상품 카테고리", example = "상의")
    val category: String,

    @NotBlank(message = "상품 가격는 필수에요.")
    @Schema(description = "상품 가격", example = "31000")
    val price: Int,

    @NotNull(message = "구매하는 스토어는 필수에요.")
    @Schema(description = "구매하는 스토어", example = "MUSINSA")
    val store: Store,

    @NotBlank(message = "상품 옵션 1은 필수에요.")
    @Schema(description = "구매 옵션 1", example = "(헤링본)화이트")
    val option1: String,

    @Schema(description = "구매 옵션 2", example = "M")
    val option2: String?,

    @Schema(description = "구매 옵션 3", example = "해당 상품은 없습니다.")
    val option3: String?
)