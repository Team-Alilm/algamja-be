package notification.controller.docs

import common.response.ApiResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.team_alilm.common.security.CustomMemberDetails
import org.team_alilm.notification.controller.dto.response.RecentNotificationResponseList
import org.team_alilm.notification.controller.dto.response.UnreadNotificationCountResponse

@Tag(name = "Notification", description = "알림 관련 API")
interface NotificationDocs {

    @Operation(
        summary = "읽지 않은 알림 개수 조회",
        description = "사용자의 읽지 않은 알림(Notification)의 총 개수를 조회합니다."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "정상 응답"
    )
    fun getUnreadNotificationCount(
        @Parameter(hidden = true) customMemberDetails: CustomMemberDetails
    ): ApiResponse<UnreadNotificationCountResponse>

    @Operation(
        summary = "한달 이내 알림 조회",
        description = "사용자의 한달 이내 알림(Notification)을 조회합니다."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "정상 응답"
    )
    fun getRecentNotifications(
        @Parameter(hidden = true) customMemberDetails: CustomMemberDetails
    ): ApiResponse<RecentNotificationResponseList>

    @Operation(
        summary = "알림 읽음 처리",
        description = "사용자의 알림(Notification)을 읽음 처리합니다."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "정상 응답",
    )
    fun readNotification(
        notificationId: Long,
        @Parameter(hidden = true) customMemberDetails: CustomMemberDetails
    ): ApiResponse<Unit>

    @Operation(
        summary = "모든 알림 읽음 처리",
        description = "사용자의 모든 알림(Notification)을 읽음 처리합니다."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "정상 응답",
    )
    fun readAllNotifications(
        @Parameter(hidden = true) customMemberDetails: CustomMemberDetails
    ): ApiResponse<Unit>
}