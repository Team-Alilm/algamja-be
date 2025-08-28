package org.team_alilm.algamja.notification.controller.dto.response

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "안 읽은 알림 개수 응답")
data class UnreadNotificationCountResponse(

    @Schema(description = "안 읽은 알림 개수", example = "5")
    val count: Long
)