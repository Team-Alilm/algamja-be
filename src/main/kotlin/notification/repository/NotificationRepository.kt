package org.team_alilm.notification.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.team_alilm.notification.entity.Notification

interface NotificationRepository : JpaRepository<Notification, Long> {

    fun countByMemberIdAndReadYnFalse(memberId: Long): Long
}