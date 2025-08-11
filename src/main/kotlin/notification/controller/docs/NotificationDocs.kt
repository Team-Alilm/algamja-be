package org.team_alilm.notification.controller.v1.docs

import common.response.ApiResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.team_alilm.common.security.CustomMemberDetails
import org.team_alilm.notification.controller.dto.response.UnreadNotificationCountResponse

@Tag(name = "Notification", description = "알림 관련 API")
interface NotificationDocs {

    @Operation(
        summary = "읽지 않은 알림 개수 조회",
        description = "사용자의 읽지 않은 알림(Notification)의 총 개수를 조회합니다."
    )
    fun getUnreadNotificationCount(
        customMemberDetails: CustomMemberDetails
    ): ApiResponse<UnreadNotificationCountResponse>
}