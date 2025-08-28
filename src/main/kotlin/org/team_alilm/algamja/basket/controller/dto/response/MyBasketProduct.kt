package org.team_alilm.algamja.basket.controller.dto.response

import io.swagger.v3.oas.annotations.media.Schema
import org.team_alilm.algamja.basket.entity.BasketRow
import org.team_alilm.algamja.product.entity.ProductRow

data class MyBasketProduct(

    @Schema(description = "장바구니 ID", example = "101")
    val basketId: Long,

    @Schema(description = "외부몰 상품 번호", example = "202410123456")
    val storeNumber: Long,

    @Schema(description = "상품명", example = "나이키 에어포스 1 화이트")
    val name: String,

    @Schema(description = "브랜드명", example = "NIKE")
    val brand: String,

    @Schema(description = "상품 이미지 URL", example = "https://example.com/images/product.jpg")
    val thumbnailUrl: String,

    @Schema(description = "스토어명", example = "무신사")
    val store: String,

    @Schema(description = "상품 가격(원)", example = "129000")
    val price: Long,

    @Schema(description = "재입고 알림 여부", example = "true")
    val notification: Boolean,

    @Schema(description = "재입고 알림 요청 날짜 (epoch millis)", example = "1725936123000")
    val notificationDate: Long?,

    @Schema(description = "1차 카테고리", example = "신발")
    val firstCategory: String,

    @Schema(description = "1차 옵션", example = "270mm")
    val firstOption: String?,

    @Schema(description = "2차 옵션", example = "화이트")
    val secondOption: String?,

    @Schema(description = "3차 옵션", example = "한정판")
    val thirdOption: String?,

    @Schema(description = "숨김 여부", example = "false")
    val hidden: Boolean,

    @Schema(description = "상품 대기 인원 수", example = "25")
    val waitingCount: Long,

    @Schema(description = "상품 ID", example = "301")
    val productId: Long
) {

    companion object {
        fun from(
            basketRow: BasketRow,
            productRow: ProductRow,
            waitingCount: Long
        ): MyBasketProduct {
            return MyBasketProduct(
                basketId = basketRow.id,
                storeNumber = productRow.storeNumber,
                name = productRow.name,
                brand = productRow.brand,
                thumbnailUrl = productRow.thumbnailUrl,
                store = productRow.store.name,
                price = productRow.price.toLong(),
                notification = basketRow.isNotification,
                notificationDate = basketRow.notificationDate,
                firstCategory = productRow.firstCategory,
                firstOption = productRow.firstOption,
                secondOption = productRow.secondOption,
                thirdOption = productRow.thirdOption,
                hidden = basketRow.isHidden,
                waitingCount = waitingCount,
                productId = productRow.id
            )
        }
    }
}
