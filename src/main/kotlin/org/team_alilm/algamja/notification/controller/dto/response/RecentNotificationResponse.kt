package org.team_alilm.algamja.notification.controller.dto.response

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "최근 알림 단건 응답")
data class RecentNotificationResponse(

    @Schema(description = "알림 ID")
    val notificationId: Long,

    @Schema(description = "상품 ID")
    val productId: Long,

    @Schema(description = "상품명")
    val productName: String,

    @Schema(description = "상품 썸네일 URL")
    val productThumbnailUrl: String,

    @Schema(description = "브랜드명")
    val productBrand: String,

    @Schema(description = "가격")
    val productPrice: Int,

    @Schema(description = "옵션 1")
    val firstOption: String,

    @Schema(description = "옵션 2")
    val secondOption: String?,

    @Schema(description = "옵션 3")
    val thirdOption: String?,

    @Schema(description = "읽음 여부")
    val readYn: Boolean,

    @Schema(description = "생성일 (밀리초)", example = "1672531199000")
    val createdData: Long
)
