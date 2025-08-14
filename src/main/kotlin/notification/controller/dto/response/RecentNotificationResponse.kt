package org.team_alilm.notification.controller.dto.response

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "최근 알림 단건 응답")
data class RecentNotificationResponse(

    @Schema(description = "알림 ID")
    val id: Long,

    @Schema(description = "상품 ID")
    val productId: Long,

    @Schema(description = "상품명")
    val productTitle: String,

    @Schema(description = "상품 썸네일 URL")
    val productThumbnailUrl: String,

    @Schema(description = "브랜드명")
    val brand: String,

    @Schema(description = "읽음 여부")
    val readYn: Boolean,

    @Schema(description = "생성일 (yyyy-MM-dd HH:mm)")
    val createdData: Long
)
