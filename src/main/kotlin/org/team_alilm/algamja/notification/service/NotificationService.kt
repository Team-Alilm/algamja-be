package org.team_alilm.algamja.notification.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.team_alilm.algamja.common.exception.BusinessException
import org.team_alilm.algamja.common.exception.ErrorCode
import org.team_alilm.algamja.notification.controller.dto.response.RecentNotificationResponse
import org.team_alilm.algamja.notification.controller.dto.response.RecentNotificationResponseList
import org.team_alilm.algamja.notification.controller.dto.response.UnreadNotificationCountResponse
import org.team_alilm.algamja.notification.repository.NotificationExposedRepository
import org.team_alilm.algamja.product.repository.ProductExposedRepository

@Service
@Transactional(readOnly = true)
class NotificationService(
    private val notificationExposedRepository: NotificationExposedRepository,
    private val productExposedRepository: ProductExposedRepository
) {

    private val log = org.slf4j.LoggerFactory.getLogger(NotificationService::class.java)

    fun getUnreadNotificationCount(memberId: Long): UnreadNotificationCountResponse {
        val count = notificationExposedRepository.countUnreadByMemberId(memberId)
        return UnreadNotificationCountResponse(count = count)
    }

    fun getRecentNotifications(memberId: Long): RecentNotificationResponseList {
        // 최근 30일
        val thirtyDaysMillis = 30L * 24 * 60 * 60 * 1000
        val since = System.currentTimeMillis() - thirtyDaysMillis

        val notifications = notificationExposedRepository.fetchUnreadByMemberIdCreatedAfter(
            memberId = memberId,
            createdDateExclusive = since
        )
        if (notifications.isEmpty()) {
            return RecentNotificationResponseList(emptyList())
        }

        // 관련 상품 일괄 조회 (Exposed)
        val productIds = notifications.map { it.productId }.toSet().toList()
        val products = productExposedRepository.fetchProductsByIds(productIds)
        val productById = products.associateBy { it.id }

        val result = notifications.mapNotNull { n ->
            val p = productById[n.productId] ?: run {
                log.warn("Product not found for productId=${n.productId} (notificationId=${n.id})")
                return@mapNotNull null
            }
            RecentNotificationResponse(
                id = n.id,
                productId = n.productId,
                productTitle = p.name,
                productThumbnailUrl = p.thumbnailUrl,
                brand = p.brand,
                readYn = n.readYn,
                createdData = n.createdDate
            )
        }

        return RecentNotificationResponseList(result)
    }

    @Transactional
    fun readNotification(notificationId: Long, memberId: Long) {
        // 소유자 조건으로 바로 업데이트 → 0건이면 예외
        val updated = notificationExposedRepository.markReadByIdAndMemberId(
            notificationId = notificationId,
            memberId = memberId
        )
        if (updated == 0) {
            log.warn("readNotification failed: notificationId={}, memberId={}", notificationId, memberId)
            throw BusinessException(ErrorCode.NOTIFICATION_NOT_FOUND)
        }
    }

    @Transactional
    fun readAllNotifications(memberId: Long) {
        // 읽지 않은 것만 일괄 업데이트 (영향 0건이어도 정상 처리)
        val updated = notificationExposedRepository.markAllUnreadReadByMemberId(memberId)
        log.info("readAllNotifications: memberId={}, updated={}", memberId, updated)
    }
}