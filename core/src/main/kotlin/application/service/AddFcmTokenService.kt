package org.team_alilm.application.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.team_alilm.application.port.`in`.use_case.AddFcmTokenUseCase
import org.team_alilm.application.port.out.LoadFcmTokenPort
import org.team_alilm.domain.FcmToken
import org.team_alilm.global.error.DeprecatedFcmtokenException

@Service
@Transactional(readOnly = true)
class AddFcmTokenService(
    private val addFcmTokenPort: org.team_alilm.application.port.out.AddFcmTokenPort,
    private val loadFcmTokenPort: LoadFcmTokenPort
) : AddFcmTokenUseCase {

    @Transactional
    override fun addFcmToken(command: AddFcmTokenUseCase.AddFcmTokenCommand) {
        val member = command.member
        val token = command.token
        val fcmToken = FcmToken(token, member.id!!)

        // 중복 검사
        loadFcmTokenPort.loadFcmToken(token)
            ?.let {
                throw DeprecatedFcmtokenException()
            } ?: addFcmTokenPort.addFcmToken(fcmToken)
    }

}