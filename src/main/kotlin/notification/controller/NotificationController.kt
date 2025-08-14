package org.team_alilm.notification.controller

import common.response.ApiResponse
import notification.service.NotificationService
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.team_alilm.common.exception.BusinessException
import org.team_alilm.common.exception.ErrorCode
import org.team_alilm.common.security.CustomMemberDetails
import org.team_alilm.notification.controller.dto.response.RecentNotificationResponseList
import org.team_alilm.notification.controller.dto.response.UnreadNotificationCountResponse
import org.team_alilm.notification.controller.v1.docs.NotificationDocs

@RestController
@RequestMapping("/api/v1/notifications")
class NotificationController(

    private val notificationService: NotificationService
) : NotificationDocs {

    @GetMapping("/unread-count")
    override fun getUnreadNotificationCount(
        @AuthenticationPrincipal customMemberDetails: CustomMemberDetails
    ): ApiResponse<UnreadNotificationCountResponse> {

        return ApiResponse.Companion.success(
            data = notificationService.getUnreadNotificationCount(
                memberId = customMemberDetails.member.id ?: throw BusinessException(ErrorCode.MEMBER_NOT_FOUND_ERROR)
            )
        )
    }

    @GetMapping("/recent")
    override fun getRecentNotifications(
        @AuthenticationPrincipal customMemberDetails: CustomMemberDetails
    ): ApiResponse<RecentNotificationResponseList> {

        return ApiResponse.Companion.success(
            data = notificationService.getRecentNotifications(
                memberId = customMemberDetails.member.id
                    ?: throw BusinessException(ErrorCode.MEMBER_NOT_FOUND_ERROR),
            )
        )
    }

    @PutMapping("/read/{notificationId}")
    override fun readNotification(
        @PathVariable notificationId: Long,
        @AuthenticationPrincipal customMemberDetails: CustomMemberDetails
    ): ApiResponse<Unit> {

        return ApiResponse.Companion.success(
            data = notificationService.readNotification(
                notificationId = notificationId,
                memberId = customMemberDetails.member.id ?: throw BusinessException(ErrorCode.MEMBER_NOT_FOUND_ERROR)
            )
        )
    }

    override fun readAllNotifications(customMemberDetails: CustomMemberDetails): ApiResponse<Unit> {
        return ApiResponse.Companion.success(
            data = notificationService.readAllNotifications(
                memberId = customMemberDetails.member.id ?: throw BusinessException(ErrorCode.MEMBER_NOT_FOUND_ERROR)
            )
        )
    }
}