package notification.service

import org.springframework.stereotype.Service
import org.team_alilm.notification.repository.NotificationRepository

@Service
class NotificationService(
    private val notificationRepository: NotificationRepository
) {

    fun getTotalNotificationCount(): Long {
        return notificationRepository.count()
    }
}