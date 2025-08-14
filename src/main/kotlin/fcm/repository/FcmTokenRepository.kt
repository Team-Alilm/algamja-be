package org.team_alilm.fcm.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.team_alilm.fcm.entity.FcmToken

interface FcmTokenRepository : JpaRepository<FcmToken, Long> {
}