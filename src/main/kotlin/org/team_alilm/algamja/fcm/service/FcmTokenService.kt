package org.team_alilm.algamja.fcm.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.team_alilm.algamja.fcm.repository.FcmTokenExposedRepository

@Service
@Transactional(readOnly = true)
class FcmTokenService(
    private val fcmTokenExposedRepository: FcmTokenExposedRepository
) {

    @Transactional
    fun registerFcmToken(memberId: Long, fcmToken: String) {
        fcmTokenExposedRepository.createFcmToken(memberId, fcmToken)
    }
}