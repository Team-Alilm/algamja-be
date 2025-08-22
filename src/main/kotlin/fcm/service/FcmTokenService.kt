package org.team_alilm.fcm.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.team_alilm.fcm.repository.FcmTokenExposedRepository

@Service
class FcmTokenService(
    private val fcmTokenExposedRepository: FcmTokenExposedRepository
) {

    @Transactional
    fun registerFcmToken(memberId: Long, fcmToken: String) {
        fcmTokenExposedRepository.createFcmToken(memberId, fcmToken)
    }
}