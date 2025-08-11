package notification.service

import org.springframework.stereotype.Service
import org.team_alilm.notification.controller.NotificationController.*
import org.team_alilm.notification.controller.dto.response.UnreadNotificationCountResponse
import org.team_alilm.notification.repository.NotificationRepository

@Service
class NotificationService(
    private val notificationRepository: NotificationRepository
) {

    fun getUnreadNotificationCount(memberId: Long): UnreadNotificationCountResponse {
        notificationRepository.countByMemberIdAndReadYnFalse(memberId)
        val unreadNotificationCountResponse = UnreadNotificationCountResponse(count = 1)

        return unreadNotificationCountResponse
    }
}