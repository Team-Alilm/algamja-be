package org.team_alilm.notification.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.team_alilm.common.exception.BusinessException
import org.team_alilm.common.exception.ErrorCode
import org.team_alilm.notification.controller.dto.response.RecentNotificationResponse
import org.team_alilm.notification.controller.dto.response.RecentNotificationResponseList
import org.team_alilm.notification.controller.dto.response.UnreadNotificationCountResponse
import org.team_alilm.notification.repository.NotificationRepository
import org.team_alilm.product.repository.ProductRepository

@Service
@Transactional(readOnly = true)
class NotificationService(
    private val notificationRepository: NotificationRepository,
    private val productRepository: ProductRepository
) {

    private val log = org.slf4j.LoggerFactory.getLogger(NotificationService::class.java)

    fun getUnreadNotificationCount(memberId: Long): UnreadNotificationCountResponse {
        notificationRepository.countByMemberIdAndReadYnFalse(memberId)
        val unreadNotificationCountResponse = UnreadNotificationCountResponse(count = 1)

        return unreadNotificationCountResponse
    }

    fun getRecentNotifications(
        memberId: Long,
    ): RecentNotificationResponseList {
        val notificationList = notificationRepository.findAllByMemberIdAndReadYnIsFalseAndCreatedDateAfter(
            memberId = memberId,
            createdDate = System.currentTimeMillis() - 30L * 24 * 60 * 60 * 1000
        )

        if (notificationList.isEmpty()) {
            return RecentNotificationResponseList(emptyList())
        }

        val productIds = notificationList.map { it.productId }.toSet()
        val productMap = productRepository.findAllById(productIds).associateBy { it.id!! }

        val recentNotificationResponseList = notificationList.map { notification ->
            val product = productMap[notification.productId]
                ?: throw IllegalArgumentException("Product not found for ID: ${notification.productId}")

            RecentNotificationResponse(
                id = notification.id!!,
                productId = notification.productId,
                productTitle = product.name,
                productThumbnailUrl = product.thumbnailUrl,
                brand = product.brand,
                readYn = notification.readYn,
                createdData = notification.createdDate
            )
        }

        return RecentNotificationResponseList(recentNotificationResponseList)
    }

    @Transactional
    fun readNotification(
        notificationId: Long,
        memberId: Long
    ) {

        val notification = notificationRepository.findById(notificationId)
            .orElseThrow {
                log.warn("Notification with ID $notificationId not found")
                BusinessException(errorCode = ErrorCode.NOTIFICATION_NOT_FOUND_ERROR)
            }

        if (notification.memberId != memberId) {
            log.warn("Member with ID $memberId attempted to read notification with ID $notificationId, but it does not belong to them")
            throw BusinessException(errorCode = ErrorCode.MEMBER_NOT_FOUND_ERROR)
        }

        notification.readYn = true
        notificationRepository.save(notification)
    }

    @Transactional
    fun readAllNotifications(memberId: Long) {
        val notifications = notificationRepository.findAllByMemberIdAndReadYnFalse(memberId)

        if (notifications.isEmpty()) {
            log.warn("No unread notifications found for member with ID $memberId")
            return
        }

        notifications.forEach { notification ->
            notification.readYn = true
        }

        notificationRepository.saveAll(notifications)
        log.info("All unread notifications for member with ID $memberId have been marked as read")
    }
}