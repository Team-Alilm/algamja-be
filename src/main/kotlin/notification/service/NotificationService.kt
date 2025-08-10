package notification.service

import org.springframework.stereotype.Service
import org.team_alilm.notification.controller.v1.NotificationControllerV1.*
import org.team_alilm.notification.repository.NotificationRepository

@Service
class NotificationService(
    private val notificationRepository: NotificationRepository
) {

    fun getUnreadNotificationCount(memberId: Long): UnreadNotificationCountResponse {
        notificationRepository.countByMemberIdAndReadYnFalse()
        val unreadNotificationCountResponse = UnreadNotificationCountResponse(count = 1)

        return unreadNotificationCountResponse
    }
}