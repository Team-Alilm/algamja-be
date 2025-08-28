package org.team_alilm.algamja.notification.controller.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Positive

@Schema(description = "알림 읽음 요청 DTO")
data class ReadNotificationRequest(

    @field:Positive(message = "notificationId는 1 이상의 양수여야 합니다.")
    @Schema(
        description = "읽음 처리할 알림 ID. null 또는 미제공이면 전체 읽음 처리",
        example = "123",
        nullable = true
    )
    val notificationId: Long? = null
)