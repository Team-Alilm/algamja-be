package org.team_alilm.notification.controller.v1

import common.response.ApiResponse
import notification.service.NotificationService
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.team_alilm.common.security.CustomMemberDetails
import org.team_alilm.notification.controller.v1.docs.NotificationDocsV1

@RestController
@RequestMapping("/api/v1/notifications")
class NotificationControllerV1(
    private val notificationService: NotificationService
) : NotificationDocsV1 {

    @GetMapping("/unread-count")
    override fun getUnreadNotificationCount(
        @AuthenticationPrincipal customMemberDetails: CustomMemberDetails
    ): ApiResponse<UnreadNotificationCountResponse> {
        val unreadNotificationCountResponse = notificationService.getUnreadNotificationCount(
            memberId = customMemberDetails.member.id
        ))

        return ApiResponse.success(unreadNotificationCountResponse)
    }

    data class UnreadNotificationCountResponse(
        val count: Int
    )
}