package org.team_alilm.algamja.notification.controller

import org.team_alilm.algamja.common.response.ApiResponse
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.team_alilm.algamja.common.security.CustomMemberDetails
import org.team_alilm.algamja.common.security.requireMemberId
import org.team_alilm.algamja.notification.controller.dto.response.RecentNotificationResponseList
import org.team_alilm.algamja.notification.controller.dto.response.UnreadNotificationCountResponse
import org.team_alilm.algamja.notification.controller.docs.NotificationDocs
import org.team_alilm.algamja.notification.service.NotificationService

@RestController
@RequestMapping("/api/v1/notifications")
class NotificationController(

    private val notificationService: NotificationService
) : NotificationDocs {

    @GetMapping("/unread-count")
    override fun getUnreadNotificationCount(
        @AuthenticationPrincipal customMemberDetails: CustomMemberDetails
    ): ApiResponse<UnreadNotificationCountResponse> {

        return ApiResponse.success(
            data = notificationService.getUnreadNotificationCount(
                memberId = customMemberDetails.requireMemberId()
            )
        )
    }

    @GetMapping("/recent")
    override fun getRecentNotifications(
        @AuthenticationPrincipal customMemberDetails: CustomMemberDetails
    ): ApiResponse<RecentNotificationResponseList> {

        return ApiResponse.success(
            data = notificationService.getRecentNotifications(
                memberId = customMemberDetails.requireMemberId()
            )
        )
    }

    @PutMapping("/read/{notificationId}")
    override fun readNotification(
        @PathVariable notificationId: Long,
        @AuthenticationPrincipal customMemberDetails: CustomMemberDetails
    ): ApiResponse<Unit> {

        return ApiResponse.success(
            data = notificationService.readNotification(
                notificationId = notificationId,
                memberId = customMemberDetails.requireMemberId()
            )
        )
    }

    @PutMapping("/read-all")
    override fun readAllNotifications(customMemberDetails: CustomMemberDetails): ApiResponse<Unit> {
        return ApiResponse.success(
            data = notificationService.readAllNotifications(
                memberId = customMemberDetails.requireMemberId()
            )
        )
    }
}