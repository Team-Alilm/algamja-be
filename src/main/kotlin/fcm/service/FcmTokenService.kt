package org.team_alilm.fcm.service

import org.springframework.stereotype.Service
import org.team_alilm.fcm.entity.FcmToken
import org.team_alilm.fcm.repository.FcmTokenRepository

@Service
class FcmTokenService(
    private val fcmTokenRepository: FcmTokenRepository
) {

    fun registerFcmToken(memberId: Long, fcmToken: String) {
        val fcmToken = FcmToken(token = fcmToken,memberId = memberId)

        fcmTokenRepository.save(fcmToken)
    }
}