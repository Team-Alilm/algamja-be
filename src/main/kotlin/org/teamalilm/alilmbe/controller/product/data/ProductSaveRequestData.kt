package org.teamalilm.alilmbe.controller.product.data

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import org.teamalilm.alilmbe.domain.product.entity.Store

data class ProductSaveRequestBody(
    @Schema(
        description = "상품 번호 입니다.",
        nullable = false,
        example = "3859221"
    )
    @NotBlank(message = "상품 번호는 필수에요.")
    @Size(max = 50, message = "상품 번호는 50자 이하여야 해요.")
    val number: String,

    @Schema(
        description = "상품명 입니다.",
        nullable = false,
        example = "COOL 롱 슬리브 셔츠 STYLE 3 TIPE"
    )
    @NotBlank(message = "상품명은 필수에요.")
    val name: String,

    @Schema(
        description = "상품 스토어 입니다.",
        nullable = false,
        example = "MUSINSA"
    )
    @NotBlank(message = "상품을 구매한 store는 필수에요.")
    val store: Store, // 스토어 이름

    @Schema(
        description = "상품 이미지 url 입니다.",
        nullable = false,
        example = "https://image.msscdn.net/images/goods_img/20240208/3859221/3859221_17084100068855_500.jpg"
    )
    @NotBlank(message = "이미지 url은 필수에요.")
    val imageUrl: String, // 상품 번호

    @Schema(
        description = "상품 옵션 1 입니다.",
        nullable = true,
        example = "(헤링본)화이트 or S, M"
    )
    @NotBlank(message = "옵션 1은 필수 입니다.")
    val option1: String, // 상품 사이즈

    @Schema(
        description = "상품 옵션 2 입니다.",
        nullable = true,
        example = "(헤링본)화이트 or S, M"
    )
    val option2: String?, // 상품 색상

    @Schema(
        description = "상품 옵션 3 입니다.",
        nullable = true,
        example = "해당 예제에는 없습니다."
    )
    val option3: String? // 상품 색상
)

