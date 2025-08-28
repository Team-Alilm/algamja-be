package org.team_alilm.algamja.notification.controller.dto.response

import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "최근 알림 목록 응답")
data class RecentNotificationResponseList(

    @ArraySchema(
        schema = Schema(implementation = RecentNotificationResponse::class),
        arraySchema = Schema(description = "최근 알림 목록")
    )
    val notificationResponseList: List<RecentNotificationResponse>
)
